package com.ordershopx.backend.modules.valoracion.dto.response;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValoracionResponseDTO {

    private UUID idValoracion;

    private UUID idPedido;

    private UUID idCliente;

    private UUID idRestaurante;

    private Integer puntuacion;

    private String comentario;

    private OffsetDateTime fechaCreacion;

}