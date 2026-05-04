package com.ordershopx.backend.modules.categoria.mapper;

import com.ordershopx.backend.modules.categoria.dto.request.CategoriaMenuRequestDTO;
import com.ordershopx.backend.modules.categoria.dto.response.CategoriaMenuResponseDTO;
import com.ordershopx.backend.modules.categoria.entity.CategoriaMenu;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoriaMenuMapper {

    // ENTITY → RESPONSE
    CategoriaMenuResponseDTO toResponse(CategoriaMenu entity);

    // REQUEST → ENTITY (para crear)
    @Mapping(target = "idCategoria", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    @Mapping(target = "eliminadoEn", ignore = true)
    CategoriaMenu toEntity(CategoriaMenuRequestDTO request);

    // UPDATE (merge sobre entidad existente)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idCategoria", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    @Mapping(target = "eliminadoEn", ignore = true)
    void updateEntityFromDto(CategoriaMenuRequestDTO request, @MappingTarget CategoriaMenu entity);
}