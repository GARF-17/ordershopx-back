package com.ordershopx.backend.modules.staff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevisarSolicitudCambioRequestDTO {

    @NotBlank(message = "El estado de la revisión es obligatorio")
    @Pattern(regexp = "^(APROBADA|RECHAZADA)$", message = "El estado solo puede ser APROBADA o RECHAZADA")
    private String estado;

    @Size(max = 1000, message = "El motivo de la revisión es demasiado largo")
    private String motivo;
}