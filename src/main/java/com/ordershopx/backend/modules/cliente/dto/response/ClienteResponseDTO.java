package com.ordershopx.backend.modules.cliente.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    private UUID id;
    private String nombre;
    private String apellido;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private JsonNode preferenciasJson;
    private String correoElectronico;
    private String telefono;
}
