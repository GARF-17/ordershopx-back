package com.ordershopx.backend.modules.pago.dto.request;

import com.ordershopx.backend.shared.enums.MetodoPago;
import com.ordershopx.backend.shared.enums.TipoPago;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotNull(message = "El id del pedido es obligatorio")
    private UUID idPedido;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    @NotNull(message = "El método de pago es obligatorio")
    private MetodoPago metodoPago;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @Builder.Default
    @Pattern(regexp = "PEN|USD", message = "La moneda permitida es PEN o USD")
    private String moneda = "PEN";

    @Size(max = 100, message = "El número de operación no puede superar los 100 caracteres")
    private String numeroOperacion;
}