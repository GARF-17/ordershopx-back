package com.ordershopx.backend.modules.staff.mapper;

import com.ordershopx.backend.modules.staff.dto.request.ActualizarStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.AsignarUsuarioRestauranteRequestDTO;
import com.ordershopx.backend.modules.staff.dto.response.UsuarioRestauranteResponseDTO;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioRestauranteMapper {

    @Mapping(target = "idUsuario", source = "usuario.usuarioId")
    @Mapping(target = "correoUsuario", source = "usuario.correoElectronico")
    @Mapping(target = "nombreUsuario", ignore = true)
    @Mapping(target = "idRestaurante", source = "restaurante.idUsuario")
    @Mapping(target = "nombreComercialRestaurante", source = "restaurante.nombreComercial")
    UsuarioRestauranteResponseDTO toResponse(UsuarioRestaurante entidad);

    List<UsuarioRestauranteResponseDTO> toResponseList(List<UsuarioRestaurante> entidades);

    @Mapping(target = "idUsuarioRestaurante", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    @Mapping(target = "estaActivo", ignore = true)
    UsuarioRestaurante toEntity(AsignarUsuarioRestauranteRequestDTO dto);

    @Mapping(target = "idUsuarioRestaurante", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    @Mapping(target = "esPrincipal", ignore = true)
    void updateEntity(@MappingTarget UsuarioRestaurante entidadExistente, ActualizarStaffRequestDTO dto);
}