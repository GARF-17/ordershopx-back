package com.ordershopx.backend.modules.pedido.mapper;

import com.ordershopx.backend.modules.pedido.dto.request.PedidoItemRequestDTO;
import com.ordershopx.backend.modules.pedido.entity.DetallePedido;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetallePedidoMapper {

    @Mapping(target = "idDetalle", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "nombreHistorico", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    DetallePedido toEntity(PedidoItemRequestDTO dto);

    List<DetallePedido> toEntityList(List<PedidoItemRequestDTO> items);
}