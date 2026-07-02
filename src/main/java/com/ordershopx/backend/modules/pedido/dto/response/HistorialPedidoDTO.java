package com.ordershopx.backend.modules.pedido.dto.response;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPedidoDTO {

    private String estado;
    private OffsetDateTime fechaCambio;
}