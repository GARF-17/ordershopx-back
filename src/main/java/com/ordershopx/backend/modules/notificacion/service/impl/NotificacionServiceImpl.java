package com.ordershopx.backend.modules.notificacion.service.impl;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.entity.Notificacion;
import com.ordershopx.backend.modules.notificacion.mapper.NotificacionMapper;
import com.ordershopx.backend.modules.notificacion.repository.NotificacionRepository;
import com.ordershopx.backend.modules.notificacion.service.INotificacionService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.TipoRol;
import com.ordershopx.backend.shared.enums.TipoNotificacion;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
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

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    @Override
    @Transactional
    public void crearYEnviarNotificacion(
            Usuario destinatario,
            TipoRol rolDestinatario,
            String titulo,
            String mensaje,
            TipoNotificacion tipo,
            String nombreCliente
    ) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(destinatario);
        notificacion.setRolDestinatario(rolDestinatario);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setTipo(tipo);
        notificacion.setLeida(false);

        Notificacion saved = notificacionRepository.save(notificacion);
        NotificacionResponseDTO dto = notificacionMapper.toResponse(saved);

        if (rolDestinatario == TipoRol.RESTAURANTE) {
            websocketService.enviarAlertaRestaurante(destinatario.getUsuarioId(), dto);
            log.info("Alerta de restaurante enviada y guardada: {}", titulo);

        } else if (rolDestinatario == TipoRol.COMENSAL) {
            websocketService.enviarNotificacionCliente(destinatario.getUsuarioId(), dto);
            log.info("Notificacion de comensal enviada y guardada: {}", titulo);
        }
    }

    @Override
    public List<NotificacionResponseDTO> listarPorUsuario() {
        Usuario usuario = getUsuarioAutenticado();
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuario_UsuarioIdOrderByFechaCreacionDesc(usuario.getUsuarioId());
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public List<NotificacionResponseDTO> listarPorRol(TipoRol rolDestinatario) {
        Usuario usuario = getUsuarioAutenticado();
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(
                        usuario.getUsuarioId(), rolDestinatario);
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public List<NotificacionResponseDTO> listarNoLeidas() {
        Usuario usuario = getUsuarioAutenticado();
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuario_UsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(usuario.getUsuarioId());
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public List<NotificacionResponseDTO> listarNoLeidasPorRol(TipoRol rolDestinatario) {
        Usuario usuario = getUsuarioAutenticado();
        List<Notificacion> notificaciones = notificacionRepository
                .findByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(
                        usuario.getUsuarioId(), rolDestinatario);
        return notificacionMapper.toResponseList(notificaciones);
    }

    @Override
    public long contarNoLeidas() {
        Usuario usuario = getUsuarioAutenticado();
        return notificacionRepository.countByUsuario_UsuarioIdAndLeidaFalse(usuario.getUsuarioId());
    }

    @Override
    public long contarNoLeidasPorRol(TipoRol rolDestinatario) {
        Usuario usuario = getUsuarioAutenticado();
        return notificacionRepository.countByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalse(
                usuario.getUsuarioId(), rolDestinatario);
    }

    @Override
    @Transactional
    public void marcarComoLeida(UUID idNotificacion) {
        Usuario usuario = getUsuarioAutenticado();
        Notificacion notificacion = notificacionRepository
                .findByIdNotificacionAndUsuario_UsuarioId(idNotificacion, usuario.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrada"));

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }
}