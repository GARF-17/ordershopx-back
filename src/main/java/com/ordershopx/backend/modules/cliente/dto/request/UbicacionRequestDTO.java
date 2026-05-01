package com.ordershopx.backend.modules.cliente.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UbicacionRequestDTO {

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "Latitud mínima inválida")
    @DecimalMax(value = "90.0", message = "Latitud máxima inválida")
    private BigDecimal latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "Longitud mínima inválida")
    @DecimalMax(value = "180.0", message = "Longitud máxima inválida")
    private BigDecimal longitud;
}