package com.ordershopx.backend.modules.pedido.mapper;

import com.ordershopx.backend.modules.pedido.dto.request.PedidoRequestDTO;
import com.ordershopx.backend.modules.pedido.dto.response.*;
import com.ordershopx.backend.modules.pedido.entity.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "idRestaurante",
            source = "restaurante.idUsuario")
    @Mapping(target = "nombreRestaurante",
            source = "restaurante.nombreComercial")
    PedidoResponseDTO toResponse(Pedido pedido);

    List<PedidoResponseDTO> toResponseList(List<Pedido> pedidos);

    @Mapping(target = "idProducto", source = "producto.idProducto")
    @Mapping(target = "nombreProducto", source = "nombreHistorico")
    PedidoDetalleResponseDTO toDetalleResponse(DetallePedido detalle);

    List<PedidoDetalleResponseDTO> toDetalleList(List<DetallePedido> detalles);

    @Mapping(target = "estado", source = "estado")
    @Mapping(target = "fechaCambio", source = "fechaCambio")
    HistorialPedidoDTO toHistorialDTO(HistorialPedido historial);

    List<HistorialPedidoDTO> toHistorialList(List<HistorialPedido> historial);

    @Mapping(target = "idPedido", ignore = true)
    @Mapping(target = "codigoRecojo", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "estadoPago", ignore = true)
    @Mapping(target = "ordenCola", ignore = true)
    @Mapping(target = "tiempoEstimadoMin", ignore = true)
    @Mapping(target = "horaEstimadaRecojo", ignore = true)
    @Mapping(target = "horaRealRecojo", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    //@Mapping(target = "impuestoIgv", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    @Mapping(target = "restaurante", ignore = true)
    Pedido toEntity(PedidoRequestDTO dto);
}