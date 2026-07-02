package com.ordershopx.backend.modules.pago.service;

import com.ordershopx.backend.modules.pago.dto.request.PagoRequestDTO;
import com.ordershopx.backend.modules.pago.dto.response.PagoResponseDTO;
import com.ordershopx.backend.modules.pago.dto.response.ResumenPagoPedidoDTO;

import java.util.List;
import java.util.UUID;

public interface IPagoService {

    PagoResponseDTO registrarPago(PagoRequestDTO request);
    PagoResponseDTO obtenerPagoPorId(UUID idPago);
    List<PagoResponseDTO> listarPagosPedido(UUID idPedido);
    ResumenPagoPedidoDTO obtenerResumenPago(UUID idPedido);
    List<PagoResponseDTO> listarPagosConfirmados();
}