package com.ordershopx.backend.modules.onboarding.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidarInvitacionRequestDTO {

    @NotBlank(message = "El token de invitación es obligatorio")
    private String token;

    @NotBlank(message = "El PIN es obligatorio")
    @Size(min = 6, max = 6, message = "El PIN debe tener exactamente 6 dígitos")
    private String pin;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String nuevaContrasena;
}