package com.ordershopx.backend.modules.notificacion.dto.response;

import com.ordershopx.backend.shared.enums.TipoNotificacion;
import com.ordershopx.backend.shared.enums.TipoRol;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionResponseDTO {

    private UUID idNotificacion;

    private UUID idPedido;

    private String titulo;

    private String mensaje;

    private TipoNotificacion tipo;

    private TipoRol rolDestinatario;

    private Boolean leida;

    private OffsetDateTime fechaCreacion;

    // NOMBRE DEL CLIENTE QUE REALIZÓ EL PEDIDO
    private String nombreCliente;
}