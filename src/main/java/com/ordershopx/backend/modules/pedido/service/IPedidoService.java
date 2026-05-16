package com.ordershopx.backend.modules.pedido.service;

import com.ordershopx.backend.modules.pedido.dto.request.CambiarEstadoPedidoDTO;
import com.ordershopx.backend.modules.pedido.dto.request.PedidoRequestDTO;
import com.ordershopx.backend.modules.pedido.dto.response.PedidoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IPedidoService {

    PedidoResponseDTO crearPedido(PedidoRequestDTO request);

    PedidoResponseDTO obtenerPedido(UUID idPedido, boolean incluirHistorial);

    List<PedidoResponseDTO> listarPedidosCliente();

    List<PedidoResponseDTO> listarColaRestaurante();

    PedidoResponseDTO cambiarEstado(CambiarEstadoPedidoDTO request);

    PedidoResponseDTO validarCodigoRecojo(String codigo);
}