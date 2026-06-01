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
import com.ordershopx.backend.shared.websocket.PedidoWebSocketService;

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
    private final PedidoWebSocketService pedidoWebSocketService;

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.obtenerPorCorreo(correo);
    }

    // =========================
    // CREAR PEDIDO
    // =========================
    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Restaurante restaurante = restauranteRepository.findById(request.getIdRestaurante())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        List<EstadoPedido> estadosActivos = List.of(
                EstadoPedido.EN_COLA,
                EstadoPedido.PREPARANDO
        );

        OffsetDateTime horarioCliente = request.getHorarioRecojoSeleccionado();

        OffsetDateTime inicio = horarioCliente
                .withSecond(0)
                .withNano(0)
                .withMinute((horarioCliente.getMinute() / 10) * 10);

        OffsetDateTime fin = inicio.plusMinutes(10);

        pedidoRepository.lockRestaurante(restaurante.getIdUsuario());

        long pedidosEnSlot = pedidoRepository.countByHorarioRango(
                restaurante.getIdUsuario(),
                estadosActivos,
                inicio,
                fin
        );

        if (pedidosEnSlot >= restaurante.getCapacidadCocina()) {
            throw new IllegalStateException("Slot saturado");
        }

        int orden = PedidoDomainService.calcularOrden(pedidosEnSlot);

        int tiempo = PedidoDomainService.calcularTiempo(
                orden,
                restaurante.getTiempoPreparacionMin()
        );

        OffsetDateTime horaEstimada = OffsetDateTime.now().plusMinutes(tiempo);

        Pedido pedido = new Pedido();
        pedido.setIdPedido(UUID.randomUUID());
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setCodigoRecojo(generarCodigo());
        pedido.setEstado(EstadoPedido.EN_COLA);
        pedido.setEstadoPago(EstadoPagoGlobal.PENDIENTE);
        pedido.setOrdenCola(orden);
        pedido.setTiempoEstimadoMin(tiempo);
        pedido.setHoraEstimadaRecojo(horaEstimada);
        pedido.setHorarioRecojoSeleccionado(inicio);
        pedido.setNotasCliente(request.getNotasCliente());

        BigDecimal subtotal = BigDecimal.ZERO;

        for (PedidoItemRequestDTO item : request.getItems()) {

            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            BigDecimal precio = producto.getPrecio();
            BigDecimal sub = precio.multiply(BigDecimal.valueOf(item.getCantidad()));
            subtotal = subtotal.add(sub);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setNombreHistorico(producto.getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(sub);

            pedido.addDetalle(detalle);
        }

        pedido.setSubtotal(subtotal);
        pedido.setTotal(subtotal);

        Pedido saved = pedidoRepository.save(pedido);

        registrarHistorial(saved);

        // Envía el evento al WebSocket para que React Native dispare la notificación local
        pedidoWebSocketService.notificarNuevoPedido(mapResponse(saved, true));

        return mapResponse(saved, true);
    }

    // =========================
    // CAMBIAR ESTADO
    // =========================
    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstado(CambiarEstadoPedidoDTO request) {

        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        pedido.setEstado(request.getEstado());

        Pedido saved = pedidoRepository.save(pedido);

        registrarHistorial(saved);

        // Envía el evento al WebSocket
        pedidoWebSocketService.notificarCambioEstado(mapResponse(saved, true));

        return mapResponse(saved, true);
    }

    // =========================
    // VALIDAR CÓDIGO (¡CON CORRECCIÓN DE HORA!)
    // =========================
    @Override
    @Transactional
    public PedidoResponseDTO validarCodigoRecojo(String codigo) {

        Usuario usuario = getUsuarioAutenticado();

        Pedido pedido = pedidoRepository
                .findByCodigoRecojoAndRestaurante_IdUsuario(codigo, usuario.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Código inválido"));

        pedido.setEstado(EstadoPedido.COMPLETADO);

        // Guardamos la hora exacta en la que se recogió el pedido para que la Native Query de la base de datos lo filtre correctamente como "completado hoy".
        pedido.setHoraRealRecojo(OffsetDateTime.now());

        Pedido saved = pedidoRepository.save(pedido);

        registrarHistorial(saved);

        // Envía el evento final al WebSocket
        pedidoWebSocketService.notificarCambioEstado(mapResponse(saved, true));

        return mapResponse(saved, true);
    }

    // =========================
    // LISTAR TODOS (RESTAURANTE) - NUEVO MÉTODO
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidosRestaurante() {
        Usuario usuario = getUsuarioAutenticado();

        // Invocamos la Native Query de PostgreSQL que filtra completados solo de hoy
        return pedidoRepository.findPedidosActualesByRestauranteId(usuario.getUsuarioId())
                .stream()
                .map(p -> mapResponse(p, false))
                .toList();
    }

    // =========================
    // LISTAR CLIENTE
    // =========================
    @Override
    public List<PedidoResponseDTO> listarPedidosCliente() {

        Usuario usuario = getUsuarioAutenticado();

        return pedidoRepository.findByCliente_IdUsuarioOrderByFechaCreacionDesc(usuario.getUsuarioId())
                .stream()
                .map(p -> mapResponse(p, false))
                .toList();
    }

    // =========================
    // COLA RESTAURANTE
    // =========================
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

    // =========================
    // OBTENER PEDIDO
    // =========================
    @Override
    public PedidoResponseDTO obtenerPedido(UUID idPedido, boolean incluirHistorial) {

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        return mapResponse(pedido, incluirHistorial);
    }

    // =========================
    // HELPERS
    // =========================
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