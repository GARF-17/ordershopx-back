package com.ordershopx.backend.modules.restaurante.mapper;

import com.ordershopx.backend.modules.restaurante.dto.request.RestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestauranteMapper {

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "usuario", ignore = true)

    @Mapping(target = "calificacionPromedio", ignore = true)
    @Mapping(target = "totalResenas", ignore = true)
    @Mapping(target = "imagenPortadaUrl", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "latitud", ignore = true)
    @Mapping(target = "longitud", ignore = true)

    Restaurante toEntity(RestauranteRequestDTO dto);

    @Mapping(source = "usuario.usuarioId", target = "id")
    @Mapping(source = "usuario.correoElectronico", target = "correoElectronico")
    @Mapping(source = "usuario.telefono", target = "telefono")
    @Mapping(source = "estado", target = "estado")

    RestauranteResponseDTO toResponse(Restaurante restaurante);
}