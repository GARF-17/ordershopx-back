package com.ordershopx.backend.modules.staff.mapper;

import com.ordershopx.backend.modules.staff.dto.request.CrearSolicitudCambioRequestDTO;
import com.ordershopx.backend.modules.staff.dto.response.SolicitudCambioRestauranteResponseDTO;
import com.ordershopx.backend.modules.staff.entity.SolicitudCambioRestaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitudCambioRestauranteMapper {

    @Mapping(target = "idRestaurante", source = "restaurante.idUsuario")
    @Mapping(target = "nombreComercialRestaurante", source = "restaurante.nombreComercial")
    @Mapping(target = "idAprobadoPor", source = "aprobadoPor.usuarioId")
    @Mapping(target = "nombreAprobador", source = "aprobadoPor.correoElectronico")
    SolicitudCambioRestauranteResponseDTO toResponse(SolicitudCambioRestaurante solicitud);

    @Mapping(target = "idCambio", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    @Mapping(target = "valorAnterior", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "aprobadoPor", ignore = true)
    SolicitudCambioRestaurante toEntity(CrearSolicitudCambioRequestDTO dto);
}