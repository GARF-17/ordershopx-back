package com.ordershopx.backend.modules.notificacion.mapper;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.entity.Notificacion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.ordershopx.backend.modules.pedido.entity.Pedido;

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
    @Mapping(
            target = "nombreCliente",
            source = "pedido",
            qualifiedByName = "resolverNombreCliente"
    )
    NotificacionResponseDTO toResponse(
            Notificacion notificacion
    );

    List<NotificacionResponseDTO> toResponseList(
            List<Notificacion> notificaciones
    );

    // ── HELPER ──────────────────────────────────────────────────────────────
    @Named("resolverNombreCliente")
    default String resolverNombreCliente(Pedido pedido) {

        if (pedido == null) return null;
        if (pedido.getCliente() == null) return null;

        String nombre   = pedido.getCliente().getNombre()   != null
                ? pedido.getCliente().getNombre().trim()   : "";
        String apellido = pedido.getCliente().getApellido() != null
                ? pedido.getCliente().getApellido().trim() : "";

        String nombreCompleto = (nombre + " " + apellido).trim();
        return nombreCompleto.isEmpty() ? null : nombreCompleto;
    }
}