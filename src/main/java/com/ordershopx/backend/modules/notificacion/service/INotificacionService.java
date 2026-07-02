package com.ordershopx.backend.modules.notificacion.service;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.shared.enums.TipoNotificacion;
import com.ordershopx.backend.shared.enums.RolGlobal;

import java.util.List;
import java.util.UUID;

public interface INotificacionService {

    void crearYEnviarNotificacion(Usuario destinatario, RolGlobal rolDestinatario, String titulo, String mensaje, TipoNotificacion tipo,String nombreCliente);
    List<NotificacionResponseDTO> listarPorUsuario();
    List<NotificacionResponseDTO> listarPorRol(RolGlobal rolDestinatario);
    List<NotificacionResponseDTO> listarNoLeidas();
    List<NotificacionResponseDTO> listarNoLeidasPorRol(RolGlobal rolDestinatario);
    long contarNoLeidas();
    long contarNoLeidasPorRol(RolGlobal rolDestinatario);
    void marcarComoLeida(UUID idNotificacion);
}