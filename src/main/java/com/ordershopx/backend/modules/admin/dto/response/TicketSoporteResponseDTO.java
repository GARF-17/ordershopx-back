package com.ordershopx.backend.modules.admin.dto.response;

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
public class TicketSoporteResponseDTO {
    private UUID idTicket;
    private String tipo;
    private String estado;
    private OffsetDateTime fechaCreacion;
    private String asunto;
    private String descripcion;
    private String correoUsuario;
    private String rolUsuario;
    private String respuestaAdmin;
}