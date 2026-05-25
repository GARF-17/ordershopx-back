package com.ordershopx.backend.shared.websocket;

import com.ordershopx.backend.modules.pedido.dto.response.PedidoResponseDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public PedidoWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // NUEVO PEDIDO
    public void notificarNuevoPedido(PedidoResponseDTO pedido) {
        messagingTemplate.convertAndSend(
                "/topic/pedidos/" + pedido.getIdRestaurante(),
                pedido
        );
    }

    // CAMBIO DE ESTADO
    public void notificarCambioEstado(PedidoResponseDTO pedido) {
        messagingTemplate.convertAndSend(
                "/topic/pedidos/" + pedido.getIdRestaurante(),
                pedido
        );
    }
}