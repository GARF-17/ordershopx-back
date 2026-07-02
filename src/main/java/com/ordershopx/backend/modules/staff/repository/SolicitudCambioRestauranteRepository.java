package com.ordershopx.backend.modules.staff.repository;

import com.ordershopx.backend.modules.staff.entity.SolicitudCambioRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolicitudCambioRestauranteRepository extends JpaRepository<SolicitudCambioRestaurante, UUID> {

    List<SolicitudCambioRestaurante> findByEstadoOrderByFechaCreacionAsc(String estado);
    List<SolicitudCambioRestaurante> findByRestauranteIdUsuarioOrderByFechaCreacionDesc(UUID idRestaurante);
    boolean existsByRestauranteIdUsuarioAndEstado(UUID idRestaurante, String estado);
    boolean existsByRestauranteIdUsuarioAndTipoCambioAndEstado(UUID idRestaurante, String tipoCambio, String estado);
}