package com.ordershopx.backend.modules.staff.dto.request;

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
public class ValidarInvitacionStaffRequestDTO {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "El PIN es obligatorio")
    @Size(min = 6, max = 6, message = "El PIN debe tener exactamente 6 dígitos")
    private String pin;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String nuevaContrasena;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 20, message = "Formato de DNI inválido")
    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 6, max = 20, message = "Formato de teléfono inválido")
    private String telefono;
}