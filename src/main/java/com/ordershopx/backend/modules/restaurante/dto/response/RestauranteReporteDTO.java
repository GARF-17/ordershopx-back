package com.ordershopx.backend.modules.restaurante.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteReporteDTO {
    private BigDecimal ingresos;
    private int ingresosCambio;
    private long pedidos;
    private int pedidosCambio;
    private BigDecimal ticketProm;
    private int ticketCambio;
    private long cancelados;
    private int canceladosCambio;

    private List<HoraPicoDTO> horasPico;
    private List<TopProductoDTO> topProductos;

    @Data
    @AllArgsConstructor
    public static class HoraPicoDTO {
        private String hora;
        private int valor;
    }

    @Data
    @AllArgsConstructor
    public static class TopProductoDTO {
        private String nombre;
        private int cantidad;
        private BigDecimal monto;
        private int porcentaje; // Para llenar la barrita azul
    }
}

