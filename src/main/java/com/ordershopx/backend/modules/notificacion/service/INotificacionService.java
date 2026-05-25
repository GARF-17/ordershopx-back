       package com.ordershopx.backend.modules.notificacion.service;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.shared.enums.TipoRol;

import java.util.List;
import java.util.UUID;

public interface INotificacionService {

    // LISTAR TODAS
    List<NotificacionResponseDTO> listarPorUsuario();

    // LISTAR POR ROL
    List<NotificacionResponseDTO> listarPorRol(
            TipoRol rolDestinatario
    );

    // LISTAR NO LEÍDAS
    List<NotificacionResponseDTO> listarNoLeidas();

    // LISTAR NO LEÍDAS POR ROL
    List<NotificacionResponseDTO> listarNoLeidasPorRol(
            TipoRol rolDestinatario
    );

    // CONTAR NO LEÍDAS
    long contarNoLeidas();

    // CONTAR NO LEÍDAS POR ROL
    long contarNoLeidasPorRol(
            TipoRol rolDestinatario
    );

    // MARCAR COMO LEÍDA
    void marcarComoLeida(
            UUID idNotificacion
    );
}
