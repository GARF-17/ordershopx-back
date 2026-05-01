package com.ordershopx.backend.modules.usuario.mapper;

import com.ordershopx.backend.modules.usuario.dto.response.UsuarioResponseDTO;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "usuarioId", target = "id")
    UsuarioResponseDTO toResponse(Usuario usuario);

}
