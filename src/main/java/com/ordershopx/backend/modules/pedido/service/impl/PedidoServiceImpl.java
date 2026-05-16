package com.ordershopx.backend.modules.pedido.service.impl;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.cliente.repository.ClienteRepository;
import com.ordershopx.backend.modules.pedido.domain.PedidoDomainService;
import com.ordershopx.backend.modules.pedido.dto.request.*;
import com.ordershopx.backend.modules.pedido.dto.response.*;
import com.ordershopx.backend.modules.pedido.entity.*;
import com.ordershopx.backend.modules.pedido.mapper.PedidoMapper;
import com.ordershopx.backend.modules.pedido.repository.*;
import com.ordershopx.backend.modules.producto.entity.Producto;
import com.ordershopx.backend.modules.producto.repository.ProductoRepository;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.pedido.service.IPedidoService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements IPedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final ProductoRepository productoRepository;
    private final HistorialPedidoRepository historialRepository;
    private final PedidoMapper pedidoMapper;
    private final IUsuarioService usuarioService;

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.obtenerPorCorreo(correo);
    }

    // CREAR PEDIDO
    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente no encontrado"
                        )
                );

        Restaurante restaurante = restauranteRepository.findById(
                        request.getIdRestaurante()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurante no encontrado"
                        )
                );

        List<EstadoPedido> estadosActivos = List.of(
                EstadoPedido.EN_COLA,
                EstadoPedido.PREPARANDO
        );

        OffsetDateTime horarioCliente =
                request.getHorarioRecojoSeleccionado();

        OffsetDateTime inicio = horarioCliente
                .withSecond(0)
                .withNano(0)
                .withMinute(
                        (horarioCliente.getMinute() / 10) * 10
                );

        OffsetDateTime fin = inicio.plusMinutes(10);

        pedidoRepository.lockRestaurante(
                restaurante.getIdUsuario()
        );

        long pedidosEnSlot =
                pedidoRepository.countByHorarioRango(
                        restaurante.getIdUsuario(),
                        estadosActivos,
                        inicio,
                        fin
                );

        if (pedidosEnSlot >= restaurante.getCapacidadCocina()) {

            throw new IllegalStateException(
                    "Slot de horario saturado"
            );
        }

        // CREAR SIEMPRE UN NUEVO PEDIDO
        int orden = PedidoDomainService.calcularOrden(
                pedidosEnSlot
        );

        int tiempo = PedidoDomainService.calcularTiempo(
                orden,
                restaurante.getTiempoPreparacionMin()
        );

        OffsetDateTime horaEstimada =
                OffsetDateTime.now().plusMinutes(tiempo);

        Pedido pedido = new Pedido();

        pedido.setIdPedido(UUID.randomUUID());

        pedido.setCliente(cliente);

        pedido.setRestaurante(restaurante);

        pedido.setCodigoRecojo(
                generarCodigo()
        );

        pedido.setEstado(
                EstadoPedido.EN_COLA
        );

        pedido.setEstadoPago(
                EstadoPagoGlobal.PENDIENTE
        );

        pedido.setOrdenCola(orden);

        pedido.setTiempoEstimadoMin(tiempo);

        pedido.setHoraEstimadaRecojo(
                horaEstimada
        );

        pedido.setHorarioRecojoSeleccionado(
                inicio
        );

        pedido.setNotasCliente(
                request.getNotasCliente()
        );

        pedido.setSubtotal(BigDecimal.ZERO);
        pedido.setImpuestoIgv(BigDecimal.ZERO);
        pedido.setTotal(BigDecimal.ZERO);

        // CALCULAR TOTALES
        BigDecimal subtotal = BigDecimal.ZERO;

        for (PedidoItemRequestDTO item : request.getItems()) {

            Producto producto =
                    productoRepository.findById(
                                    item.getIdProducto()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Producto no encontrado"
                                    )
                            );

            if (
                    !Boolean.TRUE.equals(
                            producto.getEstaDisponible()
                    )
                            || producto.getStock() == null
                            || producto.getStock() <= 0
            ) {

                throw new IllegalStateException(
                        "Stock agotado para: "
                                + producto.getNombre()
                );
            }

            if (
                    producto.getStock()
                            < item.getCantidad()
            ) {

                throw new IllegalStateException(
                        "Stock insuficiente para: "
                                + producto.getNombre()
                );
            }

            producto.setStock(
                    producto.getStock()
                            - item.getCantidad()
            );

            if (producto.getStock() <= 0) {
                producto.setEstaDisponible(false);
            }

            BigDecimal precioUnitario =
                    producto.getPrecio()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            BigDecimal sub =
                    precioUnitario.multiply(
                                    BigDecimal.valueOf(
                                            item.getCantidad()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            DetallePedido detalle =
                    DetallePedido.builder()
                            .pedido(pedido)
                            .producto(producto)
                            .nombreHistorico(
                                    producto.getNombre()
                            )
                            .cantidad(
                                    item.getCantidad()
                            )
                            .precioUnitario(
                                    precioUnitario
                            )
                            .subtotal(sub)
                            .build();

            pedido.getDetalles().add(detalle);
            subtotal = subtotal.add(sub);
        }

        subtotal = subtotal.setScale(
                2,
                RoundingMode.HALF_UP
        );

        BigDecimal igv = PedidoDomainService
                .calcularIgv(subtotal)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        BigDecimal total = subtotal
                .add(igv)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        pedido.setSubtotal(subtotal);
        pedido.setImpuestoIgv(igv);
        pedido.setTotal(total);

        log.info(
                "PEDIDO CALCULADO -> subtotal={}, igv={}, total={}",
                subtotal,
                igv,
                total
        );

        Pedido saved = pedidoRepository.save(pedido);

        registrarHistorial(saved);

        return mapResponse(saved, true);
    }

    // OBTENER PEDIDO
    @Override
    public PedidoResponseDTO obtenerPedido(UUID idPedido, boolean incluirHistorial) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return mapResponse(pedido, incluirHistorial);
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosCliente() {
        Usuario usuario = getUsuarioAutenticado();
        return pedidoRepository.findByCliente_IdUsuarioOrderByFechaCreacionDesc(usuario.getUsuarioId())
                .stream().map(p -> mapResponse(p, false)).toList();
    }

    // LISTAR PEDIDOS DE RESTAURANTE
    @Override
    public List<PedidoResponseDTO> listarColaRestaurante() {
        Usuario usuario = getUsuarioAutenticado();

        return pedidoRepository
                .findByRestaurante_IdUsuarioAndEstadoInOrderByOrdenColaAsc(
                        usuario.getUsuarioId(),
                        List.of(
                                EstadoPedido.EN_COLA,
                                EstadoPedido.PREPARANDO,
                                EstadoPedido.LISTO_PARA_RECOGER,
                                EstadoPedido.COMPLETADO
                        )
                )
                .stream()
                .map(p -> mapResponse(p, false))
                .toList();

    }

    // CAMBIAR ESTADO
    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstado(CambiarEstadoPedidoDTO request) {

        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (!esTransicionValida(pedido.getEstado(), request.getEstado())) {
            throw new IllegalStateException("Transición inválida");
        }

        pedido.setEstado(request.getEstado());
        registrarHistorial(pedido);

        return mapResponse(pedido, true);
    }

    @Override
    @Transactional
    public PedidoResponseDTO validarCodigoRecojo(String codigo) {

        Usuario usuario = getUsuarioAutenticado();

        Pedido pedido = pedidoRepository
                .findByCodigoRecojoAndRestaurante_IdUsuario(
                        codigo,
                        usuario.getUsuarioId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Código de recojo no encontrado"
                        )
                );

        // VALIDAR ESTADO
        if (
                pedido.getEstado() != EstadoPedido.LISTO_PARA_RECOGER
        ) {

            throw new IllegalStateException(
                    "El pedido aún no está listo para entregar"
            );
        }

        // CAMBIAR ESTADO
        pedido.setEstado(
                EstadoPedido.COMPLETADO
        );

        pedido.setHoraRealRecojo(
                OffsetDateTime.now()
        );

        registrarHistorial(pedido);

        Pedido actualizado =
                pedidoRepository.save(pedido);

        return mapResponse(actualizado, true);


    }

    // UTILIDADES
    private boolean esTransicionValida(EstadoPedido actual, EstadoPedido nuevo) {

        Map<EstadoPedido, Set<EstadoPedido>> reglas = Map.of(
                EstadoPedido.PENDIENTE, Set.of(EstadoPedido.EN_COLA, EstadoPedido.CANCELADO),
                EstadoPedido.EN_COLA, Set.of(EstadoPedido.PREPARANDO, EstadoPedido.CANCELADO),
                EstadoPedido.PREPARANDO, Set.of(EstadoPedido.LISTO_PARA_RECOGER),
                EstadoPedido.LISTO_PARA_RECOGER, Set.of(EstadoPedido.COMPLETADO),
                EstadoPedido.COMPLETADO, Set.of(),
                EstadoPedido.CANCELADO, Set.of()
        );

        return reglas.getOrDefault(actual, Set.of()).contains(nuevo);
    }

    private void registrarHistorial(Pedido pedido) {
        historialRepository.save(
                HistorialPedido.builder()
                        .pedido(pedido)
                        .estado(pedido.getEstado())
                        .fechaCambio(OffsetDateTime.now())
                        .build()
        );
    }

    private String generarCodigo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        String codigo;

        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            codigo = sb.toString();
        } while (pedidoRepository.existsByCodigoRecojo(codigo));

        return codigo;
    }

    private PedidoResponseDTO mapResponse(Pedido pedido, boolean incluirHistorial) {

        PedidoResponseDTO dto = pedidoMapper.toResponse(pedido);
        dto.setItems(pedidoMapper.toDetalleList(pedido.getDetalles()));
        if (incluirHistorial) {
            dto.setHistorial(
                    pedidoMapper.toHistorialList(
                            historialRepository.findByPedido_IdPedidoOrderByFechaCambioAsc(
                                    pedido.getIdPedido()
                            )
                    )
            );
        }

        return dto;
    }
}