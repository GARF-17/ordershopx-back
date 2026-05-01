package com.ordershopx.backend.modules.usuario.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private UUID id;
    private String correoElectronico;
    private String tipoDocumento;
    private String dni;
    private String telefono;
    private String rol;
}
