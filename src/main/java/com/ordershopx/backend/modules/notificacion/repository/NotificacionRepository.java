package com.ordershopx.backend.modules.notificacion.repository;

import com.ordershopx.backend.modules.notificacion.entity.Notificacion;
import com.ordershopx.backend.shared.enums.RolGlobal;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findByUsuario_UsuarioIdOrderByFechaCreacionDesc(UUID usuarioId);
    @EntityGraph(attributePaths = {"pedido", "pedido.cliente"})
    List<Notificacion> findByUsuario_UsuarioIdAndRolDestinatarioOrderByFechaCreacionDesc(UUID usuarioId, RolGlobal rolDestinatario);
    List<Notificacion> findByUsuario_UsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(UUID usuarioId);
    List<Notificacion> findByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(UUID usuarioId, RolGlobal rolDestinatario);
    long countByUsuario_UsuarioIdAndLeidaFalse(UUID usuarioId);
    long countByUsuario_UsuarioIdAndRolDestinatarioAndLeidaFalse(UUID usuarioId, RolGlobal rolDestinatario);
    Optional<Notificacion> findByIdNotificacionAndUsuario_UsuarioId(UUID idNotificacion, UUID usuarioId);
}