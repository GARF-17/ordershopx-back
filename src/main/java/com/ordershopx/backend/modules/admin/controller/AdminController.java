package com.ordershopx.backend.modules.admin.controller;

import com.ordershopx.backend.modules.onboarding.dto.response.AprobacionResponseDTO;
import com.ordershopx.backend.modules.onboarding.service.IOnboardingService;
import com.ordershopx.backend.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final IOnboardingService onboardingService;

    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @PostMapping("/solicitudes/{idSolicitud}/aprobar")
    public ResponseEntity<ApiResponse<AprobacionResponseDTO>> aprobarSolicitud(
            @PathVariable UUID idSolicitud,
            Principal principal) {

        String adminCorreo = principal.getName();

        log.info("event=api_admin_aprobar_solicitud id={} admin={}", idSolicitud, adminCorreo);

        AprobacionResponseDTO response = onboardingService.aprobarSolicitud(idSolicitud, adminCorreo);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Solicitud aprobada y credenciales generadas")
        );
    }
}