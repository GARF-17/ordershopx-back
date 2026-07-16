package com.ordershopx.backend.modules.admin.service.impl;

import com.ordershopx.backend.modules.admin.dto.response.TicketSoporteResponseDTO;
import com.ordershopx.backend.modules.admin.entity.TicketSoporte;
import com.ordershopx.backend.modules.admin.repository.TicketSoporteRepository;
import com.ordershopx.backend.modules.admin.service.IAdminSoporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSoporteServiceImpl implements IAdminSoporteService {

    private final TicketSoporteRepository ticketRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TicketSoporteResponseDTO> listarTickets() {
        List<TicketSoporte> tickets = ticketRepository.findAll();

        if (tickets.isEmpty()) {
            log.info("event=seed_tickets_soporte_db_vacia");
            ticketRepository.save(TicketSoporte.builder()
                    .tipo("CRITICO").estado("ABIERTO").fechaCreacion(OffsetDateTime.now())
                    .asunto("No puedo activar mi cuenta")
                    .descripcion("Estoy intentando ingresar mi PIN de activación pero la aplicación me dice que es inválido.")
                    .correoUsuario("carmen@marinaazul.pe").rolUsuario("RESTAURANTE").build());

            ticketRepository.save(TicketSoporte.builder()
                    .tipo("BUG").estado("EN_REVISION").fechaCreacion(OffsetDateTime.now().minusHours(2))
                    .asunto("El mapa no carga en Safari")
                    .descripcion("Al abrir la pantalla de mapa desde el navegador Safari en iPhone, no aparece ningún restaurante.")
                    .correoUsuario("juan.perez@email.com").rolUsuario("COMENSAL").build());

            ticketRepository.save(TicketSoporte.builder()
                    .tipo("CONSULTA").estado("RESUELTO").fechaCreacion(OffsetDateTime.now().minusDays(1))
                    .asunto("Tiempo de aprobación")
                    .descripcion("¿Cuánto tiempo toma la aprobación de mi restaurante una vez enviada la solicitud?")
                    .correoUsuario("info@tokyolima.pe").rolUsuario("RESTAURANTE").build());

            ticketRepository.save(TicketSoporte.builder()
                    .tipo("SOLICITUD").estado("ABIERTO").fechaCreacion(OffsetDateTime.now().minusDays(1))
                    .asunto("Necesito un reembolso de un producto")
                    .descripcion("El pedido JKL-8992 llegó incompleto, faltó la bebida que pagué por adelantado. Solicito reembolso.")
                    .correoUsuario("cris@gmail.com").rolUsuario("COMENSAL").build());

            tickets = ticketRepository.findAll();
        }

        return tickets.stream().map(this::convertirADTO).toList();
    }

    @Override
    @Transactional
    public TicketSoporteResponseDTO crearTicket(TicketSoporteResponseDTO ticketDTO) {
        log.info("event=service_crear_ticket_soporte usuario={}", ticketDTO.getCorreoUsuario());

        TicketSoporte nuevoTicket = TicketSoporte.builder()
                .tipo(ticketDTO.getTipo())
                .estado("ABIERTO")
                .fechaCreacion(OffsetDateTime.now())
                .asunto(ticketDTO.getAsunto())
                .descripcion(ticketDTO.getDescripcion() != null ? ticketDTO.getDescripcion() : ticketDTO.getAsunto())
                .correoUsuario(ticketDTO.getCorreoUsuario())
                .rolUsuario(ticketDTO.getRolUsuario())
                .build();

        TicketSoporte guardado = ticketRepository.save(nuevoTicket);
        return convertirADTO(guardado);
    }

    @Override
    @Transactional
    public TicketSoporteResponseDTO responderTicket(UUID idTicket, String respuesta, String estado) {
        TicketSoporte ticket = ticketRepository.findById(idTicket)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        ticket.setEstado(estado);

        if (respuesta != null && !respuesta.trim().isEmpty()) {
            ticket.setRespuestaAdmin(respuesta);
        }

        TicketSoporte guardado = ticketRepository.save(ticket);
        return convertirADTO(guardado);
    }

    private TicketSoporteResponseDTO convertirADTO(TicketSoporte t) {
        return TicketSoporteResponseDTO.builder()
                .idTicket(t.getIdTicket())
                .tipo(t.getTipo())
                .estado(t.getEstado())
                .fechaCreacion(t.getFechaCreacion())
                .asunto(t.getAsunto())
                .descripcion(t.getDescripcion())
                .correoUsuario(t.getCorreoUsuario())
                .rolUsuario(t.getRolUsuario())
                .respuestaAdmin(t.getRespuestaAdmin())
                .build();
    }
}