package com.ordershopx.backend.modules.cliente.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 150, message = "El apellido no puede superar los 150 caracteres")
    private String apellido;

}