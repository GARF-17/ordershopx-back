package com.ordershopx.backend.modules.staff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudCambioRestauranteResponseDTO {

    private UUID idCambio;
    private UUID idRestaurante;
    private String nombreComercialRestaurante;
    private String tipoCambio;
    private Map<String, Object> valorAnterior;
    private Map<String, Object> valorNuevo;
    private String motivo;
    private String estado;
    private UUID idAprobadoPor;
    private String nombreAprobador;
    private OffsetDateTime fechaCreacion;
}