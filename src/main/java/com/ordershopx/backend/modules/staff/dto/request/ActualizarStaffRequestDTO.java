package com.ordershopx.backend.modules.staff.dto.request;

import com.ordershopx.backend.shared.enums.RolRestaurante;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarStaffRequestDTO {

    @NotNull(message = "El rol del empleado es obligatorio")
    private RolRestaurante rol;

    @NotNull(message = "El estado de actividad es obligatorio")
    private Boolean estaActivo;

}