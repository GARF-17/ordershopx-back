package com.ordershopx.backend.modules.valoracion.repository;

import com.ordershopx.backend.modules.valoracion.entity.Valoracion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ValoracionRepository extends JpaRepository<Valoracion, UUID> {

    // VALIDAR SI EL PEDIDO YA TIENE VALORACIÓN
    boolean existsByPedido_IdPedido(UUID idPedido);

    // OBTENER VALORACIÓN POR PEDIDO
    Optional<Valoracion> findByPedido_IdPedido(UUID idPedido);

    // LISTAR VALORACIONES DE UN RESTAURANTE
    List<Valoracion> findByRestaurante_IdUsuarioOrderByFechaCreacionDesc(
            UUID idRestaurante
    );

    // LISTAR VALORACIONES DE UN CLIENTE
    List<Valoracion> findByCliente_IdUsuarioOrderByFechaCreacionDesc(
            UUID idCliente
    );

    // LISTAR POR PUNTUACIÓN
    List<Valoracion> findByPuntuacion(Integer puntuacion);

}