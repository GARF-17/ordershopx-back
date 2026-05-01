package com.ordershopx.backend.modules.cliente.repository;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    Optional<Cliente> findByUsuario(Usuario usuario);
}