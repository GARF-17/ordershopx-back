package com.ordershopx.backend.modules.pedido.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoItemRequestDTO {

    @NotNull(message = "Producto obligatorio")
    private UUID idProducto;

    @Min(value = 1, message = "Cantidad mínima 1")
    @Max(value = 100, message = "Cantidad máxima 100")
    private Integer cantidad;

    @DecimalMin(value = "0.01", message = "Precio inválido")
    private BigDecimal precioUnitario;
}