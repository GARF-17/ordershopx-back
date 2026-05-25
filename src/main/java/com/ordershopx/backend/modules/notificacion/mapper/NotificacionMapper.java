package com.ordershopx.backend.modules.notificacion.mapper;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.entity.Notificacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {

    @Mapping(
            target = "idPedido",
            source = "pedido.idPedido"
    )
    @Mapping(
            target = "rolDestinatario",
            source = "rolDestinatario"
    )
    NotificacionResponseDTO toResponse(
            Notificacion notificacion
    );

    List<NotificacionResponseDTO> toResponseList(
            List<Notificacion> notificaciones
    );
}
