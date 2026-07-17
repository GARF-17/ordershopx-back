package com.ordershopx.backend.modules.pedido.service.impl;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.cliente.repository.ClienteRepository;
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
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;
import com.ordershopx.backend.shared.websocket.PedidoWebSocketService;
import com.ordershopx.backend.modules.notificacion.service.INotificacionService;
import com.ordershopx.backend.shared.enums.RolGlobal;
import com.ordershopx.backend.shared.enums.TipoNotificacion;

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
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;
    private final INotificacionService notificacionService;

    private Usuario getUsuarioAutenticado() {
        return usuarioService.obtenerPorCorreo(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private UsuarioRestaurante getAsignacionStaff() {
        Usuario usuario = getUsuarioAutenticado();
        return usuarioRestauranteRepository.findFirstByUsuarioUsuarioIdAndEstaActivoTrue(usuario.getUsuarioId())
                .orElseThrow(() -> new UnauthorizedException("No estás asignado a ningún restaurante activo."));
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Cliente cliente = clienteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Restaurante restaurante = restauranteRepository.findById(request.getIdRestaurante())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        OffsetDateTime horarioCliente = request.getHorarioRecojoSeleccionado();
        OffsetDateTime inicio = horarioCliente.withSecond(0).withNano(0).withMinute((horarioCliente.getMinute() / 10) * 10);
        OffsetDateTime fin = inicio.plusMinutes(10);

        pedidoRepository.lockRestaurante(restaurante.getIdUsuario());
        long pedidosEnSlot = pedidoRepository.countByHorarioRango(restaurante.getIdUsuario(), List.of(EstadoPedido.EN_COLA, EstadoPedido.PREPARANDO), inicio, fin);
        int capacidadCocina = restaurante.getCapacidadCocina() != null ? restaurante.getCapacidadCocina() : 10;

        if (pedidosEnSlot >= capacidadCocina) {
            throw new IllegalStateException("El restaurante está a su máxima capacidad para este horario.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<DetallePedido> detallesList = new ArrayList<>();

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
            detallesList.add(detalle);
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setCodigoRecojo(generarCodigo());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setEstadoPago(EstadoPagoGlobal.PENDIENTE);
        pedido.setHorarioRecojoSeleccionado(inicio);
        pedido.setNotasCliente(request.getNotasCliente());
        pedido.setSubtotal(subtotal);
        pedido.setTotal(subtotal);

        for (DetallePedido det : detallesList) pedido.addDetalle(det);

        pedidoRepository.saveAndFlush(pedido);
        registrarHistorial(pedido, EstadoPedido.PENDIENTE);

        PedidoResponseDTO responseDTO = mapResponse(pedido, true);
        pedidoWebSocketService.notificarNuevoPedido(responseDTO);

        String nombreCompletoCliente = cliente.getNombre() +
                (cliente.getApellido() != null ? " " + cliente.getApellido() : "");

        notificacionService.crearYEnviarNotificacion(
                restaurante.getUsuario(),
                RolGlobal.STAFF_RESTAURANTE,
                "Nuevo pedido recibido",
                "Tienes un nuevo pedido en cola.",
                TipoNotificacion.NUEVO_PEDIDO,
                nombreCompletoCliente
        );

        return responseDTO;
    }

    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstado(CambiarEstadoPedidoDTO request) {
        UsuarioRestaurante asignacion = getAsignacionStaff();
        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (!pedido.getRestaurante().getIdUsuario().equals(asignacion.getRestaurante().getIdUsuario())) {
            throw new UnauthorizedException("Este pedido no pertenece a tu restaurante.");
        }

        pedido.setEstado(request.getEstado());
        Pedido saved = pedidoRepository.save(pedido);
        registrarHistorial(saved, request.getEstado());

        pedidoWebSocketService.notificarCambioEstado(mapResponse(saved, true));

        if (request.getEstado() == EstadoPedido.PREPARANDO) {
            notificacionService.crearYEnviarNotificacion(
                    saved.getCliente().getUsuario(),
                    RolGlobal.COMENSAL,
                    "Pedido en preparación",
                    "Tu pedido ya comenzó a prepararse.",
                    TipoNotificacion.PEDIDO_PREPARANDO,
                    null
            );
        } else if (request.getEstado() == EstadoPedido.LISTO_PARA_RECOGER) {
            notificacionService.crearYEnviarNotificacion(
                    saved.getCliente().getUsuario(),
                    RolGlobal.COMENSAL,
                    "Pedido listo",
                    "Tu pedido ya está listo para recoger.",
                    TipoNotificacion.PEDIDO_LISTO,
                    null
            );
        }

        return mapResponse(saved, true);
    }

    @Override
    @Transactional
    public PedidoResponseDTO validarCodigoRecojo(String codigo) {
        UsuarioRestaurante asignacion = getAsignacionStaff();
        if (asignacion.getRol() == RolRestaurante.COCINA) {
            throw new UnauthorizedException("El personal de cocina no puede entregar pedidos.");
        }

        Pedido pedido = pedidoRepository.findByCodigoRecojoAndRestaurante_IdUsuario(codigo, asignacion.getRestaurante().getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Código inválido"));

        pedido.setEstado(EstadoPedido.COMPLETADO);
        pedido.setHoraRealRecojo(OffsetDateTime.now());

        Pedido saved = pedidoRepository.save(pedido);
        registrarHistorial(saved, EstadoPedido.COMPLETADO);

        pedidoWebSocketService.notificarCambioEstado(mapResponse(saved, true));

        notificacionService.crearYEnviarNotificacion(
                saved.getCliente().getUsuario(),
                RolGlobal.COMENSAL,
                "Pedido completado",
                "Gracias por usar OrderShopX.",
                TipoNotificacion.PEDIDO_RECOGIDO,
                null
        );

        return mapResponse(saved, true);
    }

    private void registrarHistorial(Pedido pedido, EstadoPedido estado) {
        historialRepository.save(HistorialPedido.builder()
                .pedido(pedido)
                .estado(estado)
                .fechaCambio(OffsetDateTime.now())
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarPedidosRestaurante() {
        UsuarioRestaurante asignacion = getAsignacionStaff();
        return pedidoRepository.findPedidosActualesByRestauranteId(asignacion.getRestaurante().getIdUsuario())
                .stream().map(p -> mapResponse(p, false)).toList();
    }

    @Override
    public List<PedidoResponseDTO> listarPedidosCliente() {
        Usuario usuario = getUsuarioAutenticado();
        return pedidoRepository.findByCliente_IdUsuarioOrderByFechaCreacionDesc(usuario.getUsuarioId())
                .stream().map(p -> mapResponse(p, false)).toList();
    }

    @Override
    public List<PedidoResponseDTO> listarColaRestaurante() {
        UsuarioRestaurante asignacion = getAsignacionStaff();
        return pedidoRepository.findByRestaurante_IdUsuarioAndEstadoInOrderByOrdenColaAsc(
                        asignacion.getRestaurante().getIdUsuario(),
                        List.of(EstadoPedido.EN_COLA, EstadoPedido.PREPARANDO))
                .stream().map(p -> mapResponse(p, false)).toList();
    }

    @Override
    public PedidoResponseDTO obtenerPedido(UUID idPedido, boolean incluirHistorial) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return mapResponse(pedido, incluirHistorial);
    }

    private String generarCodigo() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        String codigo;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
            codigo = sb.toString();
        } while (pedidoRepository.existsByCodigoRecojo(codigo));
        return codigo;
    }

    private PedidoResponseDTO mapResponse(Pedido pedido, boolean incluirHistorial) {
        PedidoResponseDTO dto = pedidoMapper.toResponse(pedido);
        dto.setItems(pedidoMapper.toDetalleList(pedido.getDetalles()));
        if (incluirHistorial) dto.setHistorial(pedidoMapper.toHistorialList(historialRepository.findByPedido_IdPedidoOrderByFechaCambioAsc(pedido.getIdPedido())));
        return dto;
    }
}