package com.ordershopx.backend.modules.restaurante.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestauranteResponseDTO {

    private UUID id;
    private String correoElectronico;
    private String telefono;
    private String nombreComercial;
    private String razonSocial;
    private String ruc;
    private String direccionFiscal;
    private Integer tiempoPreparacionMin;
    private Integer tiempoPreparacionMax;
    private Integer capacidadCocina;
    private BigDecimal calificacionPromedio;
    private Integer totalResenas;
    private String imagenPortadaUrl;
    private String estado;
    private BigDecimal latitud;
    private BigDecimal longitud;
}