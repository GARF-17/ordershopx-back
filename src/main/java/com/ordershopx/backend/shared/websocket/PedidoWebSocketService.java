package com.ordershopx.backend.shared.websocket;

import com.ordershopx.backend.modules.pedido.dto.response.PedidoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarNuevoPedido(PedidoResponseDTO pedido) {
        emitir(pedido);
    }

    public void notificarCambioEstado(PedidoResponseDTO pedido) {
        emitir(pedido);
    }

    private void emitir(PedidoResponseDTO pedido) {
        String topicoRestaurante = "/topic/pedidos/restaurante/" + pedido.getIdRestaurante();
        String topicoCliente     = "/topic/pedidos/cliente/"     + pedido.getIdCliente();

        log.info("[WS] Emitiendo pedido {} → {} | {}",
                pedido.getIdPedido(), topicoRestaurante, topicoCliente);

        messagingTemplate.convertAndSend(topicoRestaurante, pedido);
        messagingTemplate.convertAndSend(topicoCliente,     pedido);
    }
}