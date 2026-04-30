package com.ordershopx.backend.modules.restaurante.repository;

import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestauranteRepository extends JpaRepository<Restaurante, UUID> {

    boolean existsByRuc(String ruc);

    Optional<Restaurante> findByRuc(String ruc);
}