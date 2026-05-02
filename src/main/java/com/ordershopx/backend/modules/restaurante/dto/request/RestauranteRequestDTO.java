package com.ordershopx.backend.modules.restaurante.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestauranteRequestDTO {

    @NotBlank(message = "El nombre comercial es obligatorio")
    @Size(max = 150)
    private String nombreComercial;

    private String razonSocial;

    @Pattern(regexp = "^\\d{11}$", message = "RUC inválido")
    private String ruc;

    private String direccionFiscal;

    @Min(value = 1, message = "Tiempo mínimo inválido")
    private Integer tiempoPreparacionMin;

    @Min(value = 1, message = "Tiempo máximo inválido")
    private Integer tiempoPreparacionMax;

    @Min(value = 1, message = "Capacidad inválida")
    private Integer capacidadCocina;
}