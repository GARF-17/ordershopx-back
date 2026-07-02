package com.ordershopx.backend.modules.onboarding.mapper;

import com.ordershopx.backend.modules.onboarding.dto.request.SolicitudRestauranteRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.SolicitudRestauranteResponseDTO;
import com.ordershopx.backend.modules.onboarding.entity.SolicitudRestaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SolicitudRestauranteMapper {

    @Mapping(target = "idAprobadoPor", source = "aprobadoPor.usuarioId")
    @Mapping(target = "nombreAprobador", source = "aprobadoPor.correoElectronico")
    SolicitudRestauranteResponseDTO toResponse(SolicitudRestaurante solicitud);

    List<SolicitudRestauranteResponseDTO> toResponseList(List<SolicitudRestaurante> solicitudes);

    @Mapping(target = "idSolicitud", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "motivoRechazo", ignore = true)
    @Mapping(target = "cantidadReenvios", ignore = true)
    @Mapping(target = "aprobadoPor", ignore = true)
    @Mapping(target = "fechaRevision", ignore = true)
    SolicitudRestaurante toEntity(SolicitudRestauranteRequestDTO dto);
}