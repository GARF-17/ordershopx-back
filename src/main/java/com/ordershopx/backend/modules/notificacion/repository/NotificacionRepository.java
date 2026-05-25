package com.ordershopx.backend.modules.notificacion.repository;

import com.ordershopx.backend.modules.notificacion.entity.Notificacion;
import com.ordershopx.backend.shared.enums.TipoRol;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    // TODAS LAS NOTIFICACIONES DEL USUARIO
    List<Notificacion> findByUsuario_UsuarioIdOrderByFechaCreacionDesc(
            UUID usuarioId
    );

    // TODAS LAS NOTIFICACIONES DEL USUARIO POR ROL
    List<Notificacion> findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(
            UUID usuarioId,
            TipoRol rolDestinatario
    );

    // NO LEÍDAS
    List<Notificacion> findByUsuario_UsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(
            UUID usuarioId
    );

    // NO LEÍDAS POR ROL
    List<Notificacion> findByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(
            UUID usuarioId,
            TipoRol rolDestinatario
    );

    // CONTADOR GENERAL
    long countByUsuario_UsuarioIdAndLeidaFalse(
            UUID usuarioId
    );

    // CONTADOR POR ROL
    long countByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalse(
            UUID usuarioId,
            TipoRol rolDestinatario
    );

    // BUSCAR NOTIFICACIÓN ESPECÍFICA
    Optional<Notificacion> findByIdNotificacionAndUsuario_UsuarioId(
            UUID idNotificacion,
            UUID usuarioId
    );
}