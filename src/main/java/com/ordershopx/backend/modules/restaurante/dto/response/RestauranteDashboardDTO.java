package com.ordershopx.backend.modules.restaurante.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteDashboardDTO {
    private BigDecimal ingresosTotales;
    private int porcentajeCrecimiento;

    private long pedidosHoy;
    private BigDecimal ticketPromedio;
    private long pedidosCompletados;

    private long pedidosActivos;
    private BigDecimal calificacion;
    private int totalResenas;

    private int tiempoPromedioPrep;
    private long clientesUnicos;

    private long pedidosCancelados;
}



