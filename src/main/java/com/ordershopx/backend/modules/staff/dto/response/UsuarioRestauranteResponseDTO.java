package com.ordershopx.backend.modules.staff.dto.response;

import com.ordershopx.backend.shared.enums.RolRestaurante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRestauranteResponseDTO {

    private UUID idUsuarioRestaurante;
    private UUID idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private UUID idRestaurante;
    private String nombreComercialRestaurante;
    private RolRestaurante rol;
    private Boolean esPrincipal;
    private Boolean estaActivo;
    private OffsetDateTime fechaCreacion;
}