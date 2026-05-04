package com.ordershopx.backend.modules.categoria.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaMenuResponseDTO {

    private UUID idCategoria;
    private String nombre;
    private Integer ordenVisual;
}