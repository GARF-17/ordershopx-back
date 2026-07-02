package com.ordershopx.backend.modules.staff.controller;

import com.ordershopx.backend.modules.staff.dto.request.ActualizarStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.CrearInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.ValidarInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.response.InvitacionStaffResponseDTO;
import com.ordershopx.backend.modules.staff.dto.response.UsuarioRestauranteResponseDTO;
import com.ordershopx.backend.modules.staff.service.IStaffService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@Slf4j
public class StaffController {

    private final IStaffService staffService;

    // LISTAR EMPLEADOS ACTIVOS
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/restaurante/{idRestaurante}/activos")
    public ResponseEntity<ApiResponse<List<UsuarioRestauranteResponseDTO>>> listarStaffActivo(
            @PathVariable UUID idRestaurante) {

        log.info("event=api_listar_staff_activo restaurante={}", idRestaurante);

        List<UsuarioRestauranteResponseDTO> response = staffService.listarStaffActivo(idRestaurante);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Staff activo obtenido correctamente")
        );
    }

    // LISTAR INVITACIONES PENDIENTES (Solo el Dueño / Staff)
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/restaurante/{idRestaurante}/invitaciones")
    public ResponseEntity<ApiResponse<List<InvitacionStaffResponseDTO>>> listarInvitacionesPendientes(
            @PathVariable UUID idRestaurante) {

        log.info("event=api_listar_invitaciones_pendientes restaurante={}", idRestaurante);

        List<InvitacionStaffResponseDTO> response = staffService.listarInvitacionesPendientes(idRestaurante);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Invitaciones pendientes obtenidas correctamente")
        );
    }

    @PreAuthorize("isAuthenticated()") //
    @PostMapping("/invitar")
    public ResponseEntity<ApiResponse<Void>> invitarEmpleado(
            @Valid @RequestBody CrearInvitacionStaffRequestDTO request,
            @RequestHeader("X-Owner-Id") UUID idOwner) {

        log.info("event=api_invitar_empleado owner={} restaurante={}", idOwner, request.getIdRestaurante());

        staffService.invitarEmpleado(request, idOwner);
        return ResponseEntity.status(201)
                .body(ApiResponse.created(null, "Invitación enviada correctamente"));
    }

    // 4. VALIDAR INVITACIÓN Y REGISTRARSE
    @PostMapping("/validar-invitacion")
    public ResponseEntity<ApiResponse<Void>> validarInvitacion(
            @Valid @RequestBody ValidarInvitacionStaffRequestDTO request) {

        log.info("event=api_validar_invitacion_staff");

        staffService.validarInvitacion(request);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Invitación validada y empleado registrado correctamente")
        );
    }

    // ACTUALIZAR O DESACTIVAR EMPLEADO
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PutMapping("/restaurante/{idRestaurante}/empleado/{idUsuarioTarget}")
    public ResponseEntity<ApiResponse<Void>> actualizarEmpleado(
            @PathVariable UUID idRestaurante,
            @PathVariable UUID idUsuarioTarget,
            @Valid @RequestBody ActualizarStaffRequestDTO request,
            @RequestHeader("X-Owner-Id") UUID idOwner) {

        log.info("event=api_actualizar_empleado target={} owner={} restaurante={}", idUsuarioTarget, idOwner, idRestaurante);

        staffService.actualizarEmpleado(idRestaurante, idUsuarioTarget, request, idOwner);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Empleado actualizado correctamente")
        );
    }
}