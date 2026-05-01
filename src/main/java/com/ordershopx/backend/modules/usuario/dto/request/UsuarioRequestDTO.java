package com.ordershopx.backend.modules.usuario.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRequestDTO {

    @Pattern(regexp = "\\d{9}", message = "El número de teléfono debe contener 9 dígitos")
    private String telefono;
}
