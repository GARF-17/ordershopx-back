package com.ordershopx.backend.modules.pago.service;

import com.ordershopx.backend.modules.pago.dto.request.PagoRequestDTO;
import com.ordershopx.backend.modules.pago.dto.response.PagoResponseDTO;
import com.ordershopx.backend.modules.pago.dto.response.ResumenPagoPedidoDTO;

import java.util.List;
import java.util.UUID;

public interface IPagoService {


     //REGISTRAR PAGO

    PagoResponseDTO registrarPago(PagoRequestDTO request);

    // OBTENER PAGO POR ID
    PagoResponseDTO obtenerPagoPorId(UUID idPago);

    // LISTAR PAGOS POR PEDIDO
    List<PagoResponseDTO> listarPagosPedido(UUID idPedido);

    // OBTENER RESUMEN DE PAGOS
    ResumenPagoPedidoDTO obtenerResumenPago(UUID idPedido);

    // LISTAR PAGOS CONFIRMADOS
    List<PagoResponseDTO> listarPagosConfirmados();

}