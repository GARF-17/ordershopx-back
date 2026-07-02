package com.ordershopx.backend.modules.pago.service.impl;

import com.ordershopx.backend.modules.pago.dto.request.PagoRequestDTO;
import com.ordershopx.backend.modules.pago.dto.response.PagoResponseDTO;
import com.ordershopx.backend.modules.pago.dto.response.ResumenPagoPedidoDTO;
import com.ordershopx.backend.modules.pago.entity.Pago;
import com.ordershopx.backend.modules.pago.mapper.PagoMapper;
import com.ordershopx.backend.modules.pago.mapper.ResumenPagoMapper;
import com.ordershopx.backend.modules.pago.repository.PagoRepository;
import com.ordershopx.backend.modules.pago.service.IPagoService;
import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.modules.pedido.repository.PedidoRepository;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;
import com.ordershopx.backend.shared.enums.MetodoPago;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import com.ordershopx.backend.shared.enums.TipoPago;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoMapper pagoMapper;
    private final ResumenPagoMapper resumenPagoMapper;
    private final IUsuarioService usuarioService;
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;

    private Usuario getUsuarioAutenticado() {
        return usuarioService.obtenerPorCorreo(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private UsuarioRestaurante getAsignacionYValidarCaja() {
        Usuario usuario = getUsuarioAutenticado();
        UsuarioRestaurante asignacion = usuarioRestauranteRepository.findFirstByUsuarioUsuarioIdAndEstaActivoTrue(usuario.getUsuarioId())
                .orElseThrow(() -> new UnauthorizedException("No estás asignado a ningún restaurante activo."));
        if (asignacion.getRol() == RolRestaurante.COCINA) {
            throw new UnauthorizedException("El personal de cocina no está autorizado para registrar pagos.");
        }
        return asignacion;
    }

    @Override
    @Transactional
    public PagoResponseDTO registrarPago(PagoRequestDTO request) {

        UsuarioRestaurante cajero = getAsignacionYValidarCaja();
        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        if (!pedido.getRestaurante().getIdUsuario().equals(cajero.getRestaurante().getIdUsuario())) {
            throw new UnauthorizedException("Este pedido no pertenece a tu restaurante.");
        }

        boolean esEfectivo = (request.getMetodoPago() == MetodoPago.EFECTIVO);
        if (!esEfectivo && (request.getNumeroOperacion() == null || request.getNumeroOperacion().isBlank())) {
            throw new IllegalStateException("El número de operación o Nro. de Voucher es obligatorio para pagos con " + request.getMetodoPago());
        }

        if (esEfectivo) {
            request.setNumeroOperacion(null);
        }
        TipoPago tipoPago = request.getTipoPago();

        if (pagoRepository.existsByPedido_IdPedidoAndTipoPago(pedido.getIdPedido(), tipoPago)) {
            throw new IllegalStateException("El " + tipoPago + " ya fue registrado previamente para este pedido.");
        }

        BigDecimal totalPedido = pedido.getTotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoRequest = request.getMonto().setScale(2, RoundingMode.HALF_UP);
        BigDecimal montoMinimoAdelanto = totalPedido.multiply(new BigDecimal("0.50")).setScale(2, RoundingMode.HALF_UP);

        // PAGO ADELANTO
        if (tipoPago == TipoPago.ADELANTO) {
            if (montoRequest.compareTo(montoMinimoAdelanto) < 0) {
                throw new IllegalStateException("El pago de adelanto debe cubrir como mínimo el 50% del pedido.");
            }
            pedido.setEstadoPago(EstadoPagoGlobal.PARCIAL);
        }

        // PAGO COMPLETO
        if (tipoPago == TipoPago.SALDO) {
            List<Pago> pagosPrevios = pagoRepository.findByPedido_IdPedidoOrderByFechaProcesamientoAsc(pedido.getIdPedido());

            BigDecimal pagadoHastaAhora = pagosPrevios.stream()
                    .map(Pago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal restanteReal = totalPedido.subtract(pagadoHastaAhora).setScale(2, RoundingMode.HALF_UP);

            if (montoRequest.compareTo(restanteReal) != 0) {
                throw new IllegalStateException("El saldo restante a cobrar debe ser exactamente de: " + restanteReal + " " + request.getMoneda());
            }
            pedido.setEstadoPago(EstadoPagoGlobal.COMPLETADO);
        }

        Pago pago = Pago.builder()
                .pedido(pedido)
                .tipoPago(tipoPago)
                .metodoPago(request.getMetodoPago())
                .monto(montoRequest)
                .moneda(request.getMoneda())
                .numeroOperacion(request.getNumeroOperacion())
                .esConfirmado(true)
                .fechaProcesamiento(OffsetDateTime.now())
                .build();

        Pago saved = pagoRepository.save(pago);
        pedidoRepository.save(pedido);

        return pagoMapper.toResponse(saved);
    }

    @Override
    public PagoResponseDTO obtenerPagoPorId(UUID idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        return pagoMapper.toResponse(pago);
    }

    @Override
    public List<PagoResponseDTO> listarPagosPedido(UUID idPedido) {
        return pagoRepository.findByPedido_IdPedidoOrderByFechaProcesamientoAsc(idPedido)
                .stream().map(pagoMapper::toResponse).toList();
    }

    @Override
    public ResumenPagoPedidoDTO obtenerResumenPago(UUID idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        List<Pago> pagos = pagoRepository.findByPedido_IdPedidoOrderByFechaProcesamientoAsc(idPedido);
        return resumenPagoMapper.toResumen(pedido, pagos);
    }

    @Override
    @Transactional
    public List<PagoResponseDTO> listarPagosConfirmados() {
        log.info("event=listar_pagos_confirmados");
        UsuarioRestaurante cajero = getAsignacionYValidarCaja();

        return pagoRepository.findByEsConfirmadoTrue()
                .stream()
                .filter(pago -> pago.getPedido().getRestaurante().getIdUsuario().equals(cajero.getRestaurante().getIdUsuario()))
                .map(pagoMapper::toResponse)
                .toList();
    }
}