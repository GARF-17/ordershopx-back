package com.ordershopx.backend.modules.pago.repository;

import com.ordershopx.backend.modules.pago.entity.Pago;
import com.ordershopx.backend.shared.enums.MetodoPago;
import com.ordershopx.backend.shared.enums.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagoRepository extends JpaRepository<Pago, UUID> {

    boolean existsByPedido_IdPedidoAndTipoPago(UUID idPedido, TipoPago tipoPago);

    Optional<Pago> findByPedido_IdPedidoAndTipoPago(UUID idPedido, TipoPago tipoPago);

    List<Pago> findByPedido_IdPedidoOrderByFechaProcesamientoAsc(UUID idPedido);

    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    List<Pago> findByTipoPago(TipoPago tipoPago);

    List<Pago> findByEsConfirmadoTrue();
}