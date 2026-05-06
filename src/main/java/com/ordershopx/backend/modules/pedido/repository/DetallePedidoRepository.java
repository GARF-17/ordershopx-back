package com.ordershopx.backend.modules.pedido.repository;

import com.ordershopx.backend.modules.pedido.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, UUID> {

    // obtener items de un pedido
    List<DetallePedido> findByPedido_IdPedido(UUID idPedido);
}