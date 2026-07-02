package com.ordershopx.backend.modules.onboarding.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitacionRestauranteRequestDTO {

    @NotNull(message = "El ID de la solicitud es obligatorio")
    private UUID idSolicitud;

    @NotBlank(message = "El token es obligatorio")
    @Size(max = 255, message = "El token no puede exceder los 255 caracteres")
    private String token;

    @NotBlank(message = "El PIN es obligatorio")
    @Size(min = 6, max = 6, message = "El PIN debe tener exactamente 6 caracteres")
    private String pin;

    @NotNull(message = "La fecha de expiración es obligatoria")
    @Future(message = "La fecha de expiración debe ser una fecha en el futuro")
    private OffsetDateTime expiraEn;

}