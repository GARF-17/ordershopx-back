package com.ordershopx.backend.modules.cliente.repository;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
}