package com.ordershopx.backend.modules.pedido.repository;

import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.time.OffsetDateTime;
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

    // RESTAURANTE
    List<Pedido> findByRestaurante_IdUsuarioOrderByFechaCreacionDesc(UUID idRestaurante);

    List<Pedido> findByRestaurante_IdUsuarioAndEstadoInOrderByOrdenColaAsc(
            UUID idRestaurante,
            List<EstadoPedido> estados
    );

    Optional<Pedido> findByIdPedidoAndRestaurante_IdUsuario(UUID idPedido, UUID idRestaurante);

    // CÓDIGOS DE RECOJO

    Optional<Pedido> findByCodigoRecojo(String codigoRecojo);

    Optional<Pedido> findByCodigoRecojoAndRestaurante_IdUsuario(
            String codigoRecojo,
            UUID idRestaurante
    );

    boolean existsByCodigoRecojo(String codigoRecojo);

    boolean existsByCliente_IdUsuarioAndEstadoIn(UUID idCliente, List<EstadoPedido> estados);

    Optional<Pedido> findFirstByCliente_IdUsuarioAndRestaurante_IdUsuarioAndEstadoInOrderByFechaCreacionDesc(
            UUID idCliente,
            UUID idRestaurante,
            List<EstadoPedido> estados
    );

    // CAPACIDAD GLOBAL
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

    // CAPACIDAD POR HORARIO
    @Query("""
        SELECT COUNT(p)
        FROM Pedido p
        WHERE p.restaurante.idUsuario = :idRestaurante
        AND p.estado IN :estados
        AND p.horarioRecojoSeleccionado >= :inicio
        AND p.horarioRecojoSeleccionado < :fin
    """)
    long countByHorarioRango(
            @Param("idRestaurante") UUID idRestaurante,
            @Param("estados") List<EstadoPedido> estados,
            @Param("inicio") OffsetDateTime inicio,
            @Param("fin") OffsetDateTime fin
    );

    // CAPACIDAD POR HORARIO DE RECOJO SELECCIONADO
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r.idUsuario
        FROM Restaurante r
        WHERE r.idUsuario = :idRestaurante
    """)
    UUID lockRestaurante(@Param("idRestaurante") UUID idRestaurante);


}