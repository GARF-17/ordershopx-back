package com.ordershopx.backend.modules.notificacion.service.impl;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.entity.Notificacion;
import com.ordershopx.backend.modules.notificacion.mapper.NotificacionMapper;
import com.ordershopx.backend.modules.notificacion.repository.NotificacionRepository;
import com.ordershopx.backend.modules.notificacion.service.INotificacionService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.TipoRol;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements INotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;
    private final IUsuarioService usuarioService;

    private Usuario getUsuarioAutenticado() {

        String correo = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    // LISTAR TODAS
    @Override
    public List<NotificacionResponseDTO> listarPorUsuario() {

        Usuario usuario = getUsuarioAutenticado();

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuario_UsuarioIdOrderByFechaCreacionDesc(
                                usuario.getUsuarioId()
                        );

        return notificacionMapper.toResponseList(
                notificaciones
        );
    }

    // LISTAR POR ROL
    @Override
    public List<NotificacionResponseDTO> listarPorRol(
            TipoRol rolDestinatario
    ) {

        Usuario usuario = getUsuarioAutenticado();

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(
                                usuario.getUsuarioId(),
                                rolDestinatario
                        );

        return notificacionMapper.toResponseList(
                notificaciones
        );
    }

    // LISTAR NO LEÍDAS
    @Override
    public List<NotificacionResponseDTO> listarNoLeidas() {

        Usuario usuario = getUsuarioAutenticado();

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuario_UsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(
                                usuario.getUsuarioId()
                        );

        return notificacionMapper.toResponseList(
                notificaciones
        );
    }

    // LISTAR NO LEÍDAS POR ROL
    @Override
    public List<NotificacionResponseDTO> listarNoLeidasPorRol(
            TipoRol rolDestinatario
    ) {

        Usuario usuario = getUsuarioAutenticado();

        List<Notificacion> notificaciones =
                notificacionRepository
                        .findByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(
                                usuario.getUsuarioId(),
                                rolDestinatario
                        );

        return notificacionMapper.toResponseList(
                notificaciones
        );
    }

    // CONTAR NO LEÍDAS
    @Override
    public long contarNoLeidas() {

        Usuario usuario = getUsuarioAutenticado();

        return notificacionRepository
                .countByUsuario_UsuarioIdAndLeidaFalse(
                        usuario.getUsuarioId()
                );
    }

    // CONTAR NO LEÍDAS POR ROL
    @Override
    public long contarNoLeidasPorRol(
            TipoRol rolDestinatario
    ) {

        Usuario usuario = getUsuarioAutenticado();

        return notificacionRepository
                .countByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalse(
                        usuario.getUsuarioId(),
                        rolDestinatario
                );
    }

    // MARCAR COMO LEÍDA
    @Override
    @Transactional
    public void marcarComoLeida(
            UUID idNotificacion
    ) {

        Usuario usuario = getUsuarioAutenticado();

        Notificacion notificacion =
                notificacionRepository
                        .findByIdNotificacionAndUsuario_UsuarioId(
                                idNotificacion,
                                usuario.getUsuarioId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notificación no encontrada"
                                )
                        );

        notificacion.setLeida(true);

        notificacionRepository.save(
                notificacion
        );
    }
}