package com.ordershopx.backend.modules.onboarding.repository;

import com.ordershopx.backend.modules.onboarding.entity.InvitacionRestaurante;
import com.ordershopx.backend.shared.enums.EstadoInvitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitacionRestauranteRepository extends JpaRepository<InvitacionRestaurante, UUID> {

    Optional<InvitacionRestaurante> findByToken(String token);
    Optional<InvitacionRestaurante> findByTokenAndPin(String token, String pin);
    Optional<InvitacionRestaurante> findByTokenAndEstado(String token, EstadoInvitacion estado);
}