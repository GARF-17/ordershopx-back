package com.ordershopx.backend.modules.notificacion.controller;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.service.INotificacionService;
import com.ordershopx.backend.shared.enums.RolGlobal;
import com.ordershopx.backend.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@Slf4j
public class NotificacionController {

    private final INotificacionService notificacionService;

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> listarNotificaciones() {
        log.info("event=api_listar_notificaciones");
        List<NotificacionResponseDTO> response = notificacionService.listarPorUsuario();
        return ResponseEntity.ok(ApiResponse.success(response, "Notificaciones obtenidas correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @GetMapping("/rol/{rol}")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> listarPorRol(@PathVariable RolGlobal rol) {
        log.info("event=api_listar_notificaciones_por_rol rol={}", rol);
        List<NotificacionResponseDTO> response = notificacionService.listarPorRol(rol);
        return ResponseEntity.ok(ApiResponse.success(response, "Notificaciones obtenidas correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @GetMapping("/no-leidas")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> listarNoLeidas() {
        log.info("event=api_listar_notificaciones_no_leidas");
        List<NotificacionResponseDTO> response = notificacionService.listarNoLeidas();
        return ResponseEntity.ok(ApiResponse.success(response, "Notificaciones no leídas obtenidas correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @GetMapping("/rol/{rol}/no-leidas")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>> listarNoLeidasPorRol(@PathVariable RolGlobal rol) {
        log.info("event=api_listar_notificaciones_no_leidas_por_rol rol={}", rol);
        List<NotificacionResponseDTO> response = notificacionService.listarNoLeidasPorRol(rol);
        return ResponseEntity.ok(ApiResponse.success(response, "Notificaciones no leídas obtenidas correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @GetMapping("/contador")
    public ResponseEntity<ApiResponse<Long>> contarNoLeidas() {
        log.info("event=api_contar_notificaciones_no_leidas");
        long response = notificacionService.contarNoLeidas();
        return ResponseEntity.ok(ApiResponse.success(response, "Cantidad obtenida correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @GetMapping("/rol/{rol}/contador")
    public ResponseEntity<ApiResponse<Long>> contarNoLeidasPorRol(@PathVariable RolGlobal rol) {
        log.info("event=api_contar_notificaciones_por_rol rol={}", rol);
        long response = notificacionService.contarNoLeidasPorRol(rol);
        return ResponseEntity.ok(ApiResponse.success(response, "Cantidad obtenida correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE', 'ADMINISTRADOR')")
    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<ApiResponse<Void>> marcarComoLeida(@PathVariable UUID idNotificacion) {
        log.info("event=api_marcar_notificacion_leida id={}", idNotificacion);
        notificacionService.marcarComoLeida(idNotificacion);
        return ResponseEntity.ok(ApiResponse.success(null, "Notificación marcada como leída"));
    }
}