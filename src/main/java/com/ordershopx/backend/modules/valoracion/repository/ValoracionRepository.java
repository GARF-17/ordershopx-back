package com.ordershopx.backend.modules.valoracion.repository;

import com.ordershopx.backend.modules.valoracion.entity.Valoracion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ValoracionRepository extends JpaRepository<Valoracion, UUID> {

    boolean existsByPedido_IdPedido(UUID idPedido);
    Optional<Valoracion> findByPedido_IdPedido(UUID idPedido);
    List<Valoracion> findByRestaurante_IdUsuarioOrderByFechaCreacionDesc(UUID idRestaurante);
    List<Valoracion> findByCliente_IdUsuarioOrderByFechaCreacionDesc(UUID idCliente);
    List<Valoracion> findByPuntuacion(Integer puntuacion);

}