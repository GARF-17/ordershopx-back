package com.ordershopx.backend.modules.admin.repository;

import com.ordershopx.backend.modules.admin.entity.TicketSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface TicketSoporteRepository extends JpaRepository<TicketSoporte, UUID> {
}