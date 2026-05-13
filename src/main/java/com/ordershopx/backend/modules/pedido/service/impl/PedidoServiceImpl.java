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
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    // CREAR PEDIDO
    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente no encontrado"));

        Restaurante restaurante = restauranteRepository
                .findById(request.getIdRestaurante())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurante no encontrado"));

        // ==========================================
        // BUSCAR PEDIDO ACTIVO DEL MISMO RESTAURANTE
        // ==========================================
        Optional<Pedido> pedidoActivoOpt =
                pedidoRepository
                        .findFirstByCliente_IdUsuarioAndRestaurante_IdUsuarioAndEstadoInOrderByFechaCreacionDesc(
                                usuario.getUsuarioId(),
                                restaurante.getIdUsuario(),
                                List.of(
                                        EstadoPedido.EN_COLA,
                                        EstadoPedido.PREPARANDO
                                )
                        );

        Pedido pedido;

        // ==========================================
        // SI EXISTE -> REUTILIZAR PEDIDO
        // ==========================================
        if (pedidoActivoOpt.isPresent()) {

            pedido = pedidoActivoOpt.get();

        } else {

            // ==========================================
            // CREAR NUEVO PEDIDO
            // ==========================================
            List<EstadoPedido> estadosActivos = List.of(
                    EstadoPedido.EN_COLA,
                    EstadoPedido.PREPARANDO
            );

            long pedidosActivos = pedidoRepository.countPedidosActivos(
                    restaurante.getIdUsuario(),
                    estadosActivos
            );

            int orden = PedidoDomainService.calcularOrden(pedidosActivos);

            int tiempo = PedidoDomainService.calcularTiempo(
                    orden,
                    restaurante.getTiempoPreparacionMin()
            );

            OffsetDateTime horaEstimada = OffsetDateTime.now()
                    .plusMinutes(tiempo);

            pedido = new Pedido();

            pedido.setIdPedido(UUID.randomUUID());
            pedido.setCliente(cliente);
            pedido.setRestaurante(restaurante);
            pedido.setCodigoRecojo(generarCodigo());
            pedido.setEstado(EstadoPedido.EN_COLA);
            pedido.setOrdenCola(orden);
            pedido.setTiempoEstimadoMin(tiempo);
            pedido.setHoraEstimadaRecojo(horaEstimada);
            pedido.setNotasCliente(request.getNotasCliente());

            pedido.setSubtotal(BigDecimal.ZERO);
            pedido.setImpuestoIgv(BigDecimal.ZERO);
            pedido.setTotal(BigDecimal.ZERO);
        }

        // ==========================================
        // SUBTOTAL ACTUAL
        // ==========================================
        BigDecimal subtotal = pedido.getSubtotal();

        // ==========================================
        // AGREGAR ITEMS
        // ==========================================
        for (PedidoItemRequestDTO item : request.getItems()) {

            Producto producto = productoRepository
                    .findById(item.getIdProducto())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Producto no encontrado"
                            ));

            // STOCK AGOTADO
            if (
                    !Boolean.TRUE.equals(producto.getEstaDisponible()) ||
                            producto.getStock() == null ||
                            producto.getStock() <= 0
            ) {

                throw new IllegalStateException(
                        "Stock agotado para: " + producto.getNombre()
                );
            }

            // STOCK INSUFICIENTE
            if (producto.getStock() < item.getCantidad()) {

                throw new IllegalStateException(
                        "Stock insuficiente para: " + producto.getNombre()
                );
            }

            // DESCONTAR STOCK
            producto.setStock(
                    producto.getStock() - item.getCantidad()
            );

            // SI YA NO HAY STOCK -> DESHABILITAR
            if (producto.getStock() <= 0) {
                producto.setEstaDisponible(false);
            }

            BigDecimal sub = producto.getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .nombreHistorico(producto.getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecio())
                    .subtotal(sub)
                    .build();

            pedido.getDetalles().add(detalle);

            subtotal = subtotal.add(sub);
        }

        // ==========================================
        // RECALCULAR TOTALES
        // ==========================================
        BigDecimal igv = PedidoDomainService.calcularIgv(subtotal);

        pedido.setSubtotal(subtotal);
        pedido.setImpuestoIgv(igv);
        pedido.setTotal(subtotal.add(igv));

        Pedido saved = pedidoRepository.save(pedido);

        // ==========================================
        // REGISTRAR HISTORIAL SOLO SI ES NUEVO
        // ==========================================
        if (pedidoActivoOpt.isEmpty()) {
            registrarHistorial(saved);
        }

        return mapResponse(saved, true);
    }

    // OBTENER PEDIDO
    @Override
    public PedidoResponseDTO obtenerPedido(UUID idPedido, boolean incluirHistorial) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        return mapResponse(pedido, incluirHistorial);
    }

    // LISTAR CLIENTE PEDIDOS
    @Override
    public List<PedidoResponseDTO> listarPedidosCliente() {

        Usuario usuario = getUsuarioAutenticado();

        return pedidoRepository
                .findByCliente_IdUsuarioOrderByFechaCreacionDesc(usuario.getUsuarioId())
                .stream()
                .map(p -> mapResponse(p, false))
                .toList();
    }

    // LISTAR COLA RESTAURANTE
    @Override
    public List<PedidoResponseDTO> listarColaRestaurante() {

        Usuario usuario = getUsuarioAutenticado();

        return pedidoRepository
                .findByRestaurante_IdUsuarioAndEstadoInOrderByOrdenColaAsc(
                        usuario.getUsuarioId(),
                        List.of(EstadoPedido.EN_COLA, EstadoPedido.PREPARANDO)
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

    // VALIDACIÓN DE TRANSICIONES DE ESTADO
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

    // REGISTRAR HISTORIAL
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