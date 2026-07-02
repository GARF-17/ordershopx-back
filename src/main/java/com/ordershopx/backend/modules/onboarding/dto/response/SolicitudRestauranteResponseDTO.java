package com.ordershopx.backend.modules.onboarding.dto.response;

import com.ordershopx.backend.shared.enums.EstadoSolicitudRestaurante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudRestauranteResponseDTO {

    private UUID idSolicitud;
    private String ruc;
    private String nombreComercial;
    private String razonSocial;
    private String direccionFiscal;
    private String correoContacto;
    private String telefonoContacto;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String encargadoNombre;
    private String encargadoApellido;
    private String encargadoDni;
    private String encargadoTelefono;
    private String encargadoCorreo;
    private EstadoSolicitudRestaurante estado;
    private String motivoRechazo;
    private Integer cantidadReenvios;
    private UUID idAprobadoPor;
    private String nombreAprobador;
    private OffsetDateTime fechaRevision;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}