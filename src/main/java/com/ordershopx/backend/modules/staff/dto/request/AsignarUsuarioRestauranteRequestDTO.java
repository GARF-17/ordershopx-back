package com.ordershopx.backend.modules.staff.dto.request;

import com.ordershopx.backend.shared.enums.RolRestaurante;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarUsuarioRestauranteRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private UUID idUsuario;

    @NotNull(message = "El ID del restaurante es obligatorio")
    private UUID idRestaurante;

    @NotNull(message = "El rol es obligatorio")
    private RolRestaurante rol;

    @NotNull(message = "Debe especificar si este usuario es el principal del restaurante")
    private Boolean esPrincipal;

}