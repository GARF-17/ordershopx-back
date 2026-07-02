package com.ordershopx.backend.modules.staff.dto.request;

import com.ordershopx.backend.shared.enums.RolRestaurante;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearInvitacionStaffRequestDTO {

    @NotNull(message = "El ID del restaurante es obligatorio")
    private UUID idRestaurante;

    @NotBlank(message = "El correo del empleado es obligatorio")
    @Email(message = "El formato del correo no es válido")
    @Size(max = 150, message = "El correo no puede exceder los 150 caracteres")
    private String correo;

    @NotNull(message = "El rol del empleado es obligatorio")
    private RolRestaurante rol;

}