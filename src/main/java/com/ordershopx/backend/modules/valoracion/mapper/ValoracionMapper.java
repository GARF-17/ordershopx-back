package com.ordershopx.backend.modules.valoracion.mapper;

import com.ordershopx.backend.modules.valoracion.dto.request.ValoracionRequestDTO;
import com.ordershopx.backend.modules.valoracion.dto.response.ValoracionResponseDTO;
import com.ordershopx.backend.modules.valoracion.entity.Valoracion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ValoracionMapper {

    @Mapping(source = "idValoracion", target = "idValoracion")
    @Mapping(source = "pedido.idPedido", target = "idPedido")
    @Mapping(source = "cliente.idUsuario", target = "idCliente")
    @Mapping(source = "restaurante.idUsuario", target = "idRestaurante")
    ValoracionResponseDTO toResponse(Valoracion valoracion);

    @Mapping(target = "idValoracion", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    Valoracion toEntity(ValoracionRequestDTO request);

}