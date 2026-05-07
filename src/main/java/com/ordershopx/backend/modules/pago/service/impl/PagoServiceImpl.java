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
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;
import com.ordershopx.backend.shared.enums.MetodoPago;
import com.ordershopx.backend.shared.enums.TipoPago;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Override
    @Transactional
    public PagoResponseDTO registrarPago(PagoRequestDTO request) {

        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido no encontrado")
                );

        if (
                request.getMetodoPago() != MetodoPago.EFECTIVO
                        && (
                        request.getNumeroOperacion() == null
                                || request.getNumeroOperacion().isBlank()
                )
        ) {

            throw new IllegalStateException(
                    "El número de operación es obligatorio para este método de pago"
            );
        }

        TipoPago tipoPago = request.getTipoPago();

        boolean yaExiste = pagoRepository
                .existsByPedido_IdPedidoAndTipoPago(
                        pedido.getIdPedido(),
                        tipoPago
                );

        if (yaExiste) {
            throw new IllegalStateException(
                    "Este tipo de pago ya fue registrado"
            );
        }

        BigDecimal totalPedido = pedido.getTotal();

        BigDecimal montoMinimoAdelanto = totalPedido
                .multiply(new BigDecimal("0.50"));

        // VALIDAR ADELANTO
        if (tipoPago == TipoPago.ADELANTO) {

            if (request.getMonto()
                    .compareTo(montoMinimoAdelanto) < 0) {

                throw new IllegalStateException(
                        "El adelanto mínimo es del 50%"
                );
            }

            pedido.setEstadoPago(
                    EstadoPagoGlobal.PARCIAL
            );
        }

        // VALIDAR SALDO
        if (tipoPago == TipoPago.SALDO) {

            BigDecimal restante = totalPedido
                    .subtract(montoMinimoAdelanto);

            if (request.getMonto()
                    .compareTo(restante) != 0) {

                throw new IllegalStateException(
                        "El saldo restante es incorrecto"
                );
            }

            pedido.setEstadoPago(
                    EstadoPagoGlobal.COMPLETADO
            );
        }

        Pago pago = Pago.builder()
                .pedido(pedido)
                .tipoPago(tipoPago)
                .metodoPago(request.getMetodoPago())
                .monto(request.getMonto())
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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pago no encontrado")
                );

        return pagoMapper.toResponse(pago);
    }

    @Override
    public List<PagoResponseDTO> listarPagosPedido(UUID idPedido) {

        return pagoRepository
                .findByPedido_IdPedidoOrderByFechaProcesamientoAsc(idPedido)
                .stream()
                .map(pagoMapper::toResponse)
                .toList();
    }

    @Override
    public ResumenPagoPedidoDTO obtenerResumenPago(UUID idPedido) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido no encontrado")
                );

        List<Pago> pagos = pagoRepository
                .findByPedido_IdPedidoOrderByFechaProcesamientoAsc(idPedido);

        return resumenPagoMapper.toResumen(
                pedido,
                pagos
        );
    }

    @Override
    @Transactional
    public List<PagoResponseDTO> listarPagosConfirmados() {

        log.info("event=listar_pagos_confirmados");

        return pagoRepository.findByEsConfirmadoTrue()
                .stream()
                .map(pagoMapper::toResponse)
                .toList();
    }
}