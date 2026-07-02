package com.ordershopx.backend.modules.onboarding.dto.response;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AprobacionResponseDTO {

    private UUID idSolicitud;
    private String estado;
    private String pinGenerado;
    private String tokenGenerado;
    private String mensaje;
}