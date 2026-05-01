package com.ordershopx.backend.modules.cliente.mapper;

import com.ordershopx.backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.ordershopx.backend.modules.cliente.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Cliente toEntity(ClienteRequestDTO dto);

    @Mapping(source = "usuario.usuarioId", target = "id")
    @Mapping(source = "usuario.correoElectronico", target = "correoElectronico")
    @Mapping(source = "usuario.telefono", target = "telefono")
    ClienteResponseDTO toResponse(Cliente cliente);
}
