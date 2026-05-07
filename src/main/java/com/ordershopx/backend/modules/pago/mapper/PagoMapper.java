package com.ordershopx.backend.modules.pago.mapper;

import com.ordershopx.backend.modules.pago.dto.response.PagoResponseDTO;
import com.ordershopx.backend.modules.pago.entity.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "idPedido", source = "pedido.idPedido")
    @Mapping(target = "tipoPago", expression = "java(pago.getTipoPago().name())")
    PagoResponseDTO toResponse(Pago pago);

}