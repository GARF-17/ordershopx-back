package com.ordershopx.backend.modules.pago.dto.response;

import com.ordershopx.backend.shared.enums.MetodoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {

    private UUID idPago;
    private UUID idPedido;
    private String tipoPago;
    private MetodoPago metodoPago;
    private BigDecimal monto;
    private String moneda;
    private String numeroOperacion;
    private Boolean esConfirmado;
    private OffsetDateTime fechaProcesamiento;
}