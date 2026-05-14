package com.ordershopx.backend.modules.restaurante.dto.response;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDisponibleDTO {

    private OffsetDateTime hora;

    private Integer cuposDisponibles;

    private Boolean disponible;
}