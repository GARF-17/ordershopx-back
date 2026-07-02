package com.ordershopx.backend.modules.producto.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    private UUID idProducto;
    private UUID idCategoria;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean estaDisponible;
}