package com.ordershopx.backend.modules.pedido.dto.request;

import com.ordershopx.backend.shared.enums.EstadoPedido;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoPedidoDTO {

    @NotNull(message = "ID del pedido obligatorio")
    private UUID idPedido;

    @NotNull(message = "Estado obligatorio")
    private EstadoPedido estado;

    @Size(max = 255)
    private String motivo;
}