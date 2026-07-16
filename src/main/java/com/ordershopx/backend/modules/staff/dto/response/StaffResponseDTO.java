package com.ordershopx.backend.modules.staff.dto.response;
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
public class StaffResponseDTO {
    private UUID idUsuarioRestaurante;
    private UUID idUsuario;
    private String nombre;
    private String inicial;
    private String rol;
    private boolean estaActivo;
    private String correo;
    private String telefono;
    private OffsetDateTime fechaVinculacion;
    private OffsetDateTime ultimoAcceso;
}


