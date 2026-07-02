package com.ordershopx.backend.modules.pedido.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PedidoDomainService {

    public static int calcularOrden(long pedidosActivos) {
        return (int) pedidosActivos + 1;
    }
    public static int calcularTiempo(int orden, int tiempoBase) {
        return orden * tiempoBase;
    }
    public static BigDecimal calcularSubtotal(List<BigDecimal> subtotales) {
        return subtotales.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
