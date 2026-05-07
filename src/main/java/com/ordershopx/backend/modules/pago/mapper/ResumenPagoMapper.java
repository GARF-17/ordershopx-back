package com.ordershopx.backend.modules.pago.mapper;

import com.ordershopx.backend.modules.pago.dto.response.ResumenPagoPedidoDTO;
import com.ordershopx.backend.modules.pago.entity.Pago;
import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ResumenPagoMapper {

    public ResumenPagoPedidoDTO toResumen(
            Pedido pedido,
            List<Pago> pagos
    ) {

        BigDecimal montoPagado = pagos.stream()
                .filter(Pago::getEsConfirmado)
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoPendiente = pedido.getTotal()
                .subtract(montoPagado);

        boolean adelantoPagado = pagos.stream()
                .anyMatch(p ->
                        p.getTipoPago() != null
                                && p.getTipoPago().name().equals("ADELANTO")
                                && Boolean.TRUE.equals(p.getEsConfirmado())
                );

        return ResumenPagoPedidoDTO.builder()
                .totalPedido(pedido.getTotal())
                .montoPagado(montoPagado)
                .montoPendiente(
                        montoPendiente.compareTo(BigDecimal.ZERO) < 0
                                ? BigDecimal.ZERO
                                : montoPendiente
                )
                .estadoPago(
                        pedido.getEstadoPago() != null
                                ? pedido.getEstadoPago().name()
                                : EstadoPagoGlobal.PENDIENTE.name()
                )
                .adelantoPagado(adelantoPagado)
                .build();
    }
}