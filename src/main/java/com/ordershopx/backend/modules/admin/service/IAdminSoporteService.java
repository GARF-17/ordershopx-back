package com.ordershopx.backend.modules.admin.service;

import com.ordershopx.backend.modules.admin.dto.response.TicketSoporteResponseDTO;
import java.util.List;
import java.util.UUID;

public interface IAdminSoporteService {
    List<TicketSoporteResponseDTO> listarTickets();
    TicketSoporteResponseDTO crearTicket(TicketSoporteResponseDTO ticketDTO);
    TicketSoporteResponseDTO responderTicket(UUID idTicket, String respuesta, String estado);
}