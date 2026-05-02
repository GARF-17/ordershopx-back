package com.ordershopx.backend.modules.cliente.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferenciasRequestDTO {

    private JsonNode preferenciasJson;

}