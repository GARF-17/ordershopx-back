package com.ordershopx.backend.modules.staff.mapper;

import com.ordershopx.backend.modules.staff.dto.request.CrearInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.response.InvitacionStaffResponseDTO;
import com.ordershopx.backend.modules.staff.entity.InvitacionStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvitacionStaffMapper {

    @Mapping(target = "idRestaurante", source = "restaurante.idUsuario")
    @Mapping(target = "nombreComercialRestaurante", source = "restaurante.nombreComercial")
    InvitacionStaffResponseDTO toResponse(InvitacionStaff invitacion);

    List<InvitacionStaffResponseDTO> toResponseList(List<InvitacionStaff> invitaciones);

    @Mapping(target = "idInvitacion", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "pin", ignore = true)
    @Mapping(target = "expiraEn", ignore = true)
    @Mapping(target = "aceptada", ignore = true)
    InvitacionStaff toEntity(CrearInvitacionStaffRequestDTO dto);
}