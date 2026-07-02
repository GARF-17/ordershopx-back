package com.ordershopx.backend.modules.restaurante.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoRestauranteRequestDTO {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;
}