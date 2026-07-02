package com.ordershopx.backend.modules.staff.repository;

import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRestauranteRepository extends JpaRepository<UsuarioRestaurante, UUID> {

    List<UsuarioRestaurante> findByRestauranteIdUsuario(UUID idRestaurante);
    List<UsuarioRestaurante> findByRestauranteIdUsuarioAndEstaActivoTrue(UUID idRestaurante);
    Optional<UsuarioRestaurante> findByUsuarioUsuarioIdAndRestauranteIdUsuario(UUID usuarioId, UUID idRestaurante);
    List<UsuarioRestaurante> findByUsuarioUsuarioId(UUID usuarioId);
    Optional<UsuarioRestaurante> findFirstByUsuarioUsuarioIdAndEstaActivoTrue(UUID idUsuario);
    List<UsuarioRestaurante> findByRestauranteIdUsuarioAndRol(UUID idRestaurante, RolRestaurante rol);
    boolean existsByUsuarioUsuarioIdAndRestauranteIdUsuario(UUID usuarioId,UUID idRestaurante);
}