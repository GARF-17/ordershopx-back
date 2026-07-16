package com.ordershopx.backend.modules.auth.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenDTO {
    private long alertasRevision;
    private long alertasActivacion;

    private long usuariosTotal;
    private long usuariosActivos;

    private long restaurantesTotal;
    private long restaurantesActivos;
    private long restaurantesSuspendidos;

    private long solPendientes;
    private long solEnRevision;
    private long solAprobadas;
    private long solRechazadas;

    private int tasaAprobacion;
}

