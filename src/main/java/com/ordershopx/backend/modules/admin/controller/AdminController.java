package com.ordershopx.backend.modules.admin.controller;

import com.ordershopx.backend.modules.auth.dto.response.DashboardResumenDTO;
import com.ordershopx.backend.modules.auth.service.IAdminDashboardService;
import com.ordershopx.backend.modules.onboarding.dto.response.AprobacionResponseDTO;
import com.ordershopx.backend.modules.onboarding.service.IOnboardingService;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.modules.usuario.dto.response.UsuarioResponseDTO;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.response.ApiResponse;

import com.ordershopx.backend.modules.admin.dto.response.TicketSoporteResponseDTO;
import com.ordershopx.backend.modules.admin.service.IAdminSoporteService;
import com.ordershopx.backend.shared.service.GeminiAIService; // NUEVA IMPORTACION

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final IOnboardingService onboardingService;
    private final IUsuarioService usuarioService;
    private final IRestauranteService restauranteService;
    private final IAdminDashboardService dashboardService;
    private final IAdminSoporteService soporteService;

    // Inyección del servicio de IA
    private final GeminiAIService geminiService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/dashboard/resumen")
    public ResponseEntity<ApiResponse<DashboardResumenDTO>> obtenerResumenDashboard() {
        DashboardResumenDTO resumen = dashboardService.obtenerResumen();
        return ResponseEntity.ok(ApiResponse.success(resumen, "Resumen del dashboard obtenido correctamente"));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @PostMapping("/solicitudes/{idSolicitud}/aprobar")
    public ResponseEntity<ApiResponse<AprobacionResponseDTO>> aprobarSolicitud(
            @PathVariable UUID idSolicitud, Principal principal) {
        String adminCorreo = principal.getName();
        AprobacionResponseDTO response = onboardingService.aprobarSolicitud(idSolicitud, adminCorreo);
        return ResponseEntity.ok(ApiResponse.success(response, "Solicitud aprobada"));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<List<UsuarioResponseDTO>>> listarUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.listarTodosLosUsuarios();
        return ResponseEntity.ok(ApiResponse.success(usuarios, "Usuarios listados"));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @PatchMapping("/usuarios/{idUsuario}/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstadoUsuario(
            @PathVariable UUID idUsuario, @RequestBody Map<String, Boolean> body) {
        Boolean activo = body.get("activo");
        usuarioService.cambiarEstado(idUsuario, activo);
        return ResponseEntity.ok(ApiResponse.success(null, "Estado actualizado"));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/restaurantes")
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> listarRestaurantesModeracion() {
        List<RestauranteResponseDTO> restaurantes = restauranteService.listarRestaurantes();
        return ResponseEntity.ok(ApiResponse.success(restaurantes, "Restaurantes listados"));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @PatchMapping("/restaurantes/{idRestaurante}/suspension")
    public ResponseEntity<ApiResponse<Void>> suspenderRestaurante(
            @PathVariable UUID idRestaurante, @RequestBody Map<String, Boolean> body) {
        Boolean suspender = body.get("suspender");
        restauranteService.suspenderRestauranteAdmin(idRestaurante, suspender);
        return ResponseEntity.ok(ApiResponse.success(null, "Estado actualizado"));
    }


    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<TicketSoporteResponseDTO>>> listarTicketsSoporte() {
        List<TicketSoporteResponseDTO> tickets = soporteService.listarTickets();
        return ResponseEntity.ok(ApiResponse.success(tickets, "Tickets de soporte obtenidos correctamente"));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/tickets/crear")
    public ResponseEntity<ApiResponse<TicketSoporteResponseDTO>> crearTicketSoporte(
            @RequestBody TicketSoporteResponseDTO request) {
        TicketSoporteResponseDTO ticketCreado = soporteService.crearTicket(request);
        return ResponseEntity.ok(ApiResponse.success(ticketCreado, "Su ticket de soporte ha sido registrado con éxito"));
    }

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @PatchMapping("/tickets/{idTicket}/responder")
    public ResponseEntity<ApiResponse<TicketSoporteResponseDTO>> responderTicket(
            @PathVariable UUID idTicket, @RequestBody Map<String, String> body) {
        String respuesta = body.get("respuestaAdmin");
        String estado = body.get("estado");
        TicketSoporteResponseDTO ticketActualizado = soporteService.responderTicket(idTicket, respuesta, estado);
        return ResponseEntity.ok(ApiResponse.success(ticketActualizado, "Ticket actualizado con éxito"));
    }


    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @GetMapping("/tickets/{idTicket}/sugerir-respuesta")
    public ResponseEntity<ApiResponse<String>> sugerirRespuestaIA(@PathVariable UUID idTicket) {
        log.info("event=api_admin_sugerir_respuesta_ia idTicket={}", idTicket);


        TicketSoporteResponseDTO ticket = soporteService.listarTickets().stream()
                .filter(t -> t.getIdTicket().equals(idTicket))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));


        String sugerencia = geminiService.generarRespuesta(ticket.getDescripcion());

        return ResponseEntity.ok(ApiResponse.success(sugerencia, "Respuesta generada por IA"));
    }
}