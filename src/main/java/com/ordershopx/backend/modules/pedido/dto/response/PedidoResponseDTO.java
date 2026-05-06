package com.ordershopx.backend.modules.pedido.dto.response;

import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {

    private UUID idPedido;

    private String codigoRecojo;

    private EstadoPedido estado;

    private EstadoPagoGlobal estadoPago;

    private Integer ordenCola;

    private Integer tiempoEstimadoMin;

    private OffsetDateTime horaEstimadaRecojo;

    private OffsetDateTime horaRealRecojo;

    private BigDecimal subtotal;

    private BigDecimal impuestoIgv;

    private BigDecimal total;

    private String notasCliente;

    // items del pedido
    private List<PedidoDetalleResponseDTO> items;

    // historial (OPCIONAL)
    private List<HistorialPedidoDTO> historial;
}