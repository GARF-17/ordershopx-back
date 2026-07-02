package com.ordershopx.backend.modules.onboarding.mapper;

import com.ordershopx.backend.modules.onboarding.dto.request.InvitacionRestauranteRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.InvitacionRestauranteResponseDTO;
import com.ordershopx.backend.modules.onboarding.entity.InvitacionRestaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvitacionRestauranteMapper {

    @Mapping(target = "idSolicitud", source = "solicitud.idSolicitud")
    @Mapping(target = "nombreComercialRestaurante", source = "solicitud.nombreComercial")
    InvitacionRestauranteResponseDTO toResponse(InvitacionRestaurante invitacion);

    List<InvitacionRestauranteResponseDTO> toResponseList(List<InvitacionRestaurante> invitaciones);

    @Mapping(target = "idInvitacion", ignore = true)
    @Mapping(target = "solicitud", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "usadoEn", ignore = true)
    InvitacionRestaurante toEntity(InvitacionRestauranteRequestDTO dto);
}