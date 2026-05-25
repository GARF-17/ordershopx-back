package com.ordershopx.backend.modules.auth.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private String correoElectronico;
    private String rol;
    private java.util.UUID idRestaurante; // ← nuevo campo
}
