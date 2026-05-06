package com.ordershopx.backend.modules.pedido.repository;

import com.ordershopx.backend.modules.pedido.entity.HistorialPedido;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialPedidoRepository extends JpaRepository<HistorialPedido, UUID> {

    // historial completo de un pedido
    List<HistorialPedido> findByPedido_IdPedidoOrderByFechaCambioAsc(UUID idPedido);

    //  últimos estados de un pedido
    List<HistorialPedido> findByPedido_IdPedidoOrderByFechaCambioDesc(UUID idPedido);
}