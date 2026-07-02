package com.ordershopx.backend.modules.notificacion.dto.response;

import com.ordershopx.backend.shared.enums.TipoNotificacion;
import com.ordershopx.backend.shared.enums.RolGlobal;

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
    private RolGlobal rolDestinatario;
    private Boolean leida;
    private OffsetDateTime fechaCreacion;
    private String nombreCliente;
}