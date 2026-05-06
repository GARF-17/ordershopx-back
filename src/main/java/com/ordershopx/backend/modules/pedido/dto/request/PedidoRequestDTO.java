package com.ordershopx.backend.modules.pedido.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {

    @NotNull(message = "El restaurante es obligatorio")
    private UUID idRestaurante;

    @NotEmpty(message = "El pedido debe tener al menos un producto")
    private List<PedidoItemRequestDTO> items;

    @Size(max = 255, message = "Máximo 255 caracteres en notas")
    private String notasCliente;
}