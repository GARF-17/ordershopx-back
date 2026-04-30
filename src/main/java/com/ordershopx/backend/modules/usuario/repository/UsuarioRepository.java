package com.ordershopx.backend.modules.usuario.repository;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // BÚSQUEDAS
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);

    Optional<Usuario> findByDni(String dni);

    // VALIDACIONES UNIQUE
    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByDni(String dni);

    boolean existsByTelefono(String telefono);

    // OPCIONAL
    boolean existsByCorreoElectronicoOrDni(String correoElectronico, String dni);
}