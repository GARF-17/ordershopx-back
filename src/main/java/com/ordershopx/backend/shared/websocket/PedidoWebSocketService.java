package com.ordershopx.backend.shared.websocket;

import com.ordershopx.backend.modules.pedido.dto.response.PedidoResponseDTO;
import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarNuevoPedido(PedidoResponseDTO pedido) {
        emitirPedido(pedido);
    }
    public void notificarCambioEstado(PedidoResponseDTO pedido) {
        emitirPedido(pedido);
    }

    private void emitirPedido(PedidoResponseDTO pedido) {
        String topicoRestaurante = "/topic/pedidos/restaurante/" + pedido.getIdRestaurante();
        String topicoCliente     = "/topic/pedidos/cliente/"     + pedido.getIdCliente();

        log.info("[WS] Emitiendo PEDIDO {} → {} | {}",
                pedido.getIdPedido(), topicoRestaurante, topicoCliente);

        messagingTemplate.convertAndSend(topicoRestaurante, pedido);
        messagingTemplate.convertAndSend(topicoCliente,     pedido);
    }

    public void enviarAlertaRestaurante(UUID idRestaurante, NotificacionResponseDTO notificacion) {
        String topico = "/topic/notificaciones/restaurante/" + idRestaurante;

        log.info("[WS] Emitiendo ALERTA URGENTE a Restaurante → {}", topico);
        messagingTemplate.convertAndSend(topico, notificacion);
    }

    public void enviarNotificacionCliente(UUID idCliente, NotificacionResponseDTO notificacion) {
        String topico = "/topic/notificaciones/cliente/" + idCliente;
        log.info("[WS] Emitiendo NOTIFICACIÓN a Cliente → {}", topico);
        messagingTemplate.convertAndSend(topico, notificacion);
    }
}