package com.ordershopx.backend.modules.pago.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenPagoPedidoDTO {

    private BigDecimal totalPedido;
    private BigDecimal montoPagado;
    private BigDecimal montoPendiente;
    private String estadoPago;
    private Boolean adelantoPagado;
}