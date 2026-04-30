package com.ordershopx.backend.modules.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDTO {
    private String correoElectronico;
    private String mensaje;
}
