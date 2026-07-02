package com.ordershopx.backend.modules.auth.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private String correoElectronico;
    private String rol;
    private UUID idRestaurante;
    private UUID idUsuario;

}
