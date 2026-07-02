package com.ordershopx.backend.modules.staff.dto.response;

import com.ordershopx.backend.shared.enums.RolRestaurante;
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
public class InvitacionStaffResponseDTO {

    private UUID idInvitacion;
    private UUID idRestaurante;
    private String nombreComercialRestaurante;
    private String correo;
    private RolRestaurante rol;
    private OffsetDateTime expiraEn;
    private Boolean aceptada;
    private OffsetDateTime fechaCreacion;
}