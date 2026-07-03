package com.ordershopx.backend.modules.onboarding.controller;

import com.ordershopx.backend.modules.onboarding.dto.request.SolicitudRestauranteRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.request.ValidarInvitacionRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.SolicitudRestauranteResponseDTO;
import com.ordershopx.backend.modules.onboarding.service.IOnboardingService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/onboarding")
@RequiredArgsConstructor
@Slf4j
public class OnboardingController {

    private final IOnboardingService onboardingService;

    @PostMapping("/solicitud")
    public ResponseEntity<ApiResponse<SolicitudRestauranteResponseDTO>> registrarSolicitud(
            @Valid @RequestBody SolicitudRestauranteRequestDTO request) {
        log.info("event=api_registrar_solicitud_restaurante ruc={}", request.getRuc());
        SolicitudRestauranteResponseDTO response = onboardingService.registrarSolicitud(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(response, "Solicitud de restaurante enviada correctamente"));
    }

    @PostMapping("/validar-invitacion")
    public ResponseEntity<ApiResponse<Void>> validarInvitacion(
            @Valid @RequestBody ValidarInvitacionRequestDTO request) {
        log.info("event=api_validar_invitacion_onboarding");
        onboardingService.validarInvitacion(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Cuenta del restaurante activada correctamente")
        );
    }

    @GetMapping("/estado/{solicitudId}")
    public ResponseEntity<ApiResponse<SolicitudRestauranteResponseDTO>> consultarEstado(
            @PathVariable UUID solicitudId) {
        log.info("event=api_consultar_estado_solicitud id={}", solicitudId);
        SolicitudRestauranteResponseDTO response = onboardingService.consultarEstado(solicitudId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Estado de la solicitud obtenido correctamente")
        );
    }

    @PostMapping("/solicitud/{id}/aprobar")
    public ResponseEntity<ApiResponse<Void>> aprobarSolicitud(@PathVariable UUID id) {
        log.info("event=api_aprobar_solicitud_restaurante id={}", id);
        onboardingService.aprobarSolicitudYGenerarPin(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Solicitud aprobada. El PIN ha sido enviado al correo del encargado.")
        );
    }


    @GetMapping("/solicitudes")
    public ResponseEntity<ApiResponse<List<SolicitudRestauranteResponseDTO>>> listarSolicitudes() {
        log.info("event=api_listar_solicitudes_restaurantes");
        List<SolicitudRestauranteResponseDTO> solicitudes = onboardingService.listarSolicitudes();
        return ResponseEntity.ok(
                ApiResponse.success(solicitudes, "Lista de solicitudes obtenida correctamente")
        );
    }
}