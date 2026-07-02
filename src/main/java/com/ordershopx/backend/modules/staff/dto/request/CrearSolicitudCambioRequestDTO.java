package com.ordershopx.backend.modules.staff.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearSolicitudCambioRequestDTO {

    @NotNull(message = "El ID del restaurante es obligatorio")
    private UUID idRestaurante;

    @NotBlank(message = "El tipo de cambio es obligatorio")
    @Size(max = 100, message = "El tipo de cambio no puede exceder los 100 caracteres")
    private String tipoCambio;

    @NotNull(message = "El nuevo valor es obligatorio")
    private Map<String, Object> valorNuevo;

    private String motivo;

}