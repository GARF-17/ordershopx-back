package com.ordershopx.backend.modules.pedido.repository;

import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    // CLIENTE
    List<Pedido> findByCliente_IdUsuarioOrderByFechaCreacionDesc(UUID idCliente);

    List<Pedido> findByCliente_IdUsuarioAndEstadoInOrderByFechaCreacionDesc(
            UUID idCliente,
            List<EstadoPedido> estados
    );

    Optional<Pedido> findTopByCliente_IdUsuarioOrderByFechaCreacionDesc(UUID idCliente);

    Optional<Pedido> findByIdPedidoAndCliente_IdUsuario(UUID idPedido, UUID idCliente);

    //  RESTAURANTE
    List<Pedido> findByRestaurante_IdUsuarioOrderByFechaCreacionDesc(UUID idRestaurante);

    List<Pedido> findByRestaurante_IdUsuarioAndEstadoInOrderByOrdenColaAsc(
            UUID idRestaurante,
            List<EstadoPedido> estados
    );

    Optional<Pedido> findByIdPedidoAndRestaurante_IdUsuario(UUID idPedido, UUID idRestaurante);

    // TRACKING / BÚSQUEDA
    Optional<Pedido> findByCodigoRecojo(String codigoRecojo);

    boolean existsByCodigoRecojo(String codigoRecojo);

    // MÉTRICA CLAVE (COLA)

    @Query("""
        SELECT COUNT(p)
        FROM Pedido p
        WHERE p.restaurante.idUsuario = :idRestaurante
        AND p.estado IN :estados
    """)
    long countPedidosActivos(
            @Param("idRestaurante") UUID idRestaurante,
            @Param("estados") List<EstadoPedido> estados
    );
}