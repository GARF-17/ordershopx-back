package com.ordershopx.backend.modules.notificacion.service.impl;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.entity.Notificacion;
import com.ordershopx.backend.modules.notificacion.mapper.NotificacionMapper;
import com.ordershopx.backend.modules.notificacion.repository.NotificacionRepository;
import com.ordershopx.backend.modules.notificacion.service.INotificacionService;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.RolGlobal;
import com.ordershopx.backend.shared.enums.TipoNotificacion;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;
import com.ordershopx.backend.shared.websocket.PedidoWebSocketService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;
    private final IUsuarioService usuarioService;
    private final PedidoWebSocketService websocketService;
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioService.obtenerPorCorreo(correo);
    }

    private UUID obtenerIdBuzon(Usuario usuario) {
        if (usuario.getRol() == RolGlobal.STAFF_RESTAURANTE) {
            UsuarioRestaurante asignacion = usuarioRestauranteRepository.findFirstByUsuarioUsuarioIdAndEstaActivoTrue(usuario.getUsuarioId())
                    .orElseThrow(() -> new UnauthorizedException("No estás asignado a ningún restaurante activo."));
            return asignacion.getRestaurante().getIdUsuario();
        }
        return usuario.getUsuarioId();
    }

    @Override
    @Transactional
    public void crearYEnviarNotificacion(Usuario destinatario, RolGlobal rolDestinatario, String titulo, String mensaje, TipoNotificacion tipo, String nombreCliente) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(destinatario);
        notificacion.setRolDestinatario(rolDestinatario);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);

        Notificacion saved = notificacionRepository.save(notificacion);
        NotificacionResponseDTO dto = notificacionMapper.toResponse(saved);

        if (rolDestinatario == RolGlobal.STAFF_RESTAURANTE) {
            websocketService.enviarAlertaRestaurante(destinatario.getUsuarioId(), dto);
            log.info("Alerta de restaurante enviada y guardada: {}", titulo);

        } else if (rolDestinatario == RolGlobal.COMENSAL) {
            websocketService.enviarNotificacionCliente(destinatario.getUsuarioId(), dto);
            log.info("Notificacion de comensal enviada y guardada: {}", titulo);
        }
    }

    @Override
    public List<NotificacionResponseDTO> listarPorUsuario() {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);
        List<Notificacion> notificaciones = notificacionRepository.findByUsuario_UsuarioIdOrderByFechaCreacionDesc(idBuzon);
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public List<NotificacionResponseDTO> listarPorRol(RolGlobal rolDestinatario) {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);
        List<Notificacion> notificaciones = notificacionRepository.findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(idBuzon, rolDestinatario);
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public List<NotificacionResponseDTO> listarNoLeidas() {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);
        List<Notificacion> notificaciones = notificacionRepository.findByUsuario_UsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(idBuzon);
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public List<NotificacionResponseDTO> listarNoLeidasPorRol(RolGlobal rolDestinatario) {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);
        List<Notificacion> notificaciones = notificacionRepository.findByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(idBuzon, rolDestinatario);
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public long contarNoLeidas() {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);
        return notificacionRepository.countByUsuario_UsuarioIdAndLeidaFalse(idBuzon);
    }

    @Override
    public long contarNoLeidasPorRol(RolGlobal rolDestinatario) {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);
        return notificacionRepository.countByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalse(idBuzon, rolDestinatario);
    }

    @Override
    @Transactional
    public void marcarComoLeida(UUID idNotificacion) {
        Usuario usuario = getUsuarioAutenticado();
        UUID idBuzon = obtenerIdBuzon(usuario);

        Notificacion notificacion = notificacionRepository.findByIdNotificacionAndUsuario_UsuarioId(idNotificacion, idBuzon)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada o no pertenece a tu buzón"));

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }
}