package com.ordershopx.backend.modules.onboarding.repository;

import com.ordershopx.backend.modules.onboarding.entity.SolicitudRestaurante;
import com.ordershopx.backend.shared.enums.EstadoSolicitudRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SolicitudRestauranteRepository extends JpaRepository<SolicitudRestaurante, UUID> {

    boolean existsByRuc(String ruc);
    Optional<SolicitudRestaurante> findByRuc(String ruc);
    List<SolicitudRestaurante> findByEstado(EstadoSolicitudRestaurante estado);
    List<SolicitudRestaurante> findByEstadoOrderByFechaCreacionAsc(EstadoSolicitudRestaurante estado);
}