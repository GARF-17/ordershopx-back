package com.ordershopx.backend.modules.notificacion.controller;

import com.ordershopx.backend.modules.notificacion.dto.response.NotificacionResponseDTO;
import com.ordershopx.backend.modules.notificacion.service.INotificacionService;
import com.ordershopx.backend.shared.enums.TipoRol;
import com.ordershopx.backend.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@Slf4j
public class NotificacionController {

    private final INotificacionService notificacionService;

    // LISTAR TODAS
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>>
    listarNotificaciones() {

        log.info(
                "event=api_listar_notificaciones"
        );

        List<NotificacionResponseDTO> response =
                notificacionService.listarPorUsuario();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Notificaciones obtenidas correctamente"
                )
        );
    }

    // LISTAR POR ROL
    @GetMapping("/rol/{rol}")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>>
    listarPorRol(
            @PathVariable TipoRol rol
    ) {

        log.info(
                "event=api_listar_notificaciones_por_rol rol={}",
                rol
        );

        List<NotificacionResponseDTO> response =
                notificacionService.listarPorRol(
                        rol
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Notificaciones obtenidas correctamente"
                )
        );
    }

    // LISTAR NO LEÍDAS
    @GetMapping("/no-leidas")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>>
    listarNoLeidas() {

        log.info(
                "event=api_listar_notificaciones_no_leidas"
        );

        List<NotificacionResponseDTO> response =
                notificacionService.listarNoLeidas();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Notificaciones no leídas obtenidas correctamente"
                )
        );
    }

    // LISTAR NO LEÍDAS POR ROL
    @GetMapping("/rol/{rol}/no-leidas")
    public ResponseEntity<ApiResponse<List<NotificacionResponseDTO>>>
    listarNoLeidasPorRol(
            @PathVariable TipoRol rol
    ) {

        log.info(
                "event=api_listar_notificaciones_no_leidas_por_rol rol={}",
                rol
        );

        List<NotificacionResponseDTO> response =
                notificacionService.listarNoLeidasPorRol(
                        rol
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Notificaciones no leídas obtenidas correctamente"
                )
        );
    }

    // CONTAR NO LEÍDAS
    @GetMapping("/contador")
    public ResponseEntity<ApiResponse<Long>>
    contarNoLeidas() {

        log.info(
                "event=api_contar_notificaciones_no_leidas"
        );

        long response =
                notificacionService.contarNoLeidas();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Cantidad obtenida correctamente"
                )
        );
    }

    // CONTAR NO LEÍDAS POR ROL
    @GetMapping("/rol/{rol}/contador")
    public ResponseEntity<ApiResponse<Long>>
    contarNoLeidasPorRol(
            @PathVariable TipoRol rol
    ) {

        log.info(
                "event=api_contar_notificaciones_por_rol rol={}",
                rol
        );

        long response =
                notificacionService.contarNoLeidasPorRol(
                        rol
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Cantidad obtenida correctamente"
                )
        );
    }

    // MARCAR COMO LEÍDA
    @PatchMapping("/{idNotificacion}/leida")
    public ResponseEntity<ApiResponse<Void>>
    marcarComoLeida(
            @PathVariable UUID idNotificacion
    ) {

        log.info(
                "event=api_marcar_notificacion_leida id={}",
                idNotificacion
        );

        notificacionService.marcarComoLeida(
                idNotificacion
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Notificación marcada como leída"
                )
        );
    }
}
