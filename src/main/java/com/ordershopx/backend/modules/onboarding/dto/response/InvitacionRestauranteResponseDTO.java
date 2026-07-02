package com.ordershopx.backend.modules.onboarding.dto.response;

import com.ordershopx.backend.shared.enums.EstadoInvitacion;
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
public class InvitacionRestauranteResponseDTO {

    private UUID idInvitacion;
    private UUID idSolicitud;
    private String nombreComercialRestaurante;
    private OffsetDateTime expiraEn;
    private EstadoInvitacion estado;
    private OffsetDateTime usadoEn;
    private OffsetDateTime fechaCreacion;

}