package com.ordershopx.backend.modules.staff.repository;

import com.ordershopx.backend.modules.staff.entity.InvitacionStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitacionStaffRepository extends JpaRepository<InvitacionStaff, UUID> {

    Optional<InvitacionStaff> findByTokenAndAceptadaFalse(String token);
    Optional<InvitacionStaff> findByTokenAndPinAndAceptadaFalse(String token, String pin);
    List<InvitacionStaff> findByRestauranteIdUsuarioAndAceptadaFalse(UUID idRestaurante);
    boolean existsByRestauranteIdUsuarioAndCorreoAndAceptadaFalse(UUID idRestaurante, String correo);
}