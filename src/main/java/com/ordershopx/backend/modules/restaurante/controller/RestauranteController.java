package com.ordershopx.backend.modules.restaurante.controller;

import com.ordershopx.backend.modules.restaurante.dto.request.EstadoRestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.request.RestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.request.UbicacionRestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.HorarioDisponibleDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteDashboardDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteReporteDTO;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.modules.staff.dto.response.StaffResponseDTO;
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
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
@Slf4j
public class RestauranteController {

    private final IRestauranteService restauranteService;

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> listarRestaurantes() {
        log.info("event=api_listar_restaurantes");
        List<RestauranteResponseDTO> response = restauranteService.listarRestaurantes();
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurantes obtenidos correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE')")
    @GetMapping("/cercanos")
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> buscarRestaurantesCercanos(
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng,
            @RequestParam(value = "radio", defaultValue = "1.5") Double radio
    ) {
        log.info("event=api_buscar_restaurantes_cercanos lat={} lng={} radio={}", lat, lng, radio);
        List<RestauranteResponseDTO> response = restauranteService.buscarRestaurantesCercanos(lat, lng, radio);
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurantes cercanos obtenidos correctamente"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> obtenerMiRestaurante() {
        log.info("event=api_obtener_mi_restaurante");
        RestauranteResponseDTO response = restauranteService.obtenerMiRestaurante();
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurante obtenido correctamente"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PutMapping
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> actualizarRestaurante(
            @Valid @RequestBody RestauranteRequestDTO request
    ) {
        log.info("event=api_actualizar_restaurante");
        RestauranteResponseDTO response = restauranteService.actualizarRestaurante(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurante actualizado correctamente"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PutMapping("/ubicacion")
    public ResponseEntity<ApiResponse<Void>> actualizarUbicacion(
            @Valid @RequestBody UbicacionRestauranteRequestDTO request
    ) {
        log.info("event=api_actualizar_ubicacion_restaurante");
        restauranteService.actualizarUbicacion(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Ubicación actualizada correctamente"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PutMapping("/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @Valid @RequestBody EstadoRestauranteRequestDTO request
    ) {
        log.info("event=api_cambiar_estado estado={}", request.getEstado());
        restauranteService.cambiarEstado(request.getEstado());
        return ResponseEntity.ok(ApiResponse.success(null, "Estado actualizado correctamente"));
    }

    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE')")
    @GetMapping("/{id}/horarios")
    public ResponseEntity<ApiResponse<List<HorarioDisponibleDTO>>> listarHorariosDisponibles(
            @PathVariable UUID id
    ) {
        log.info("event=api_listar_horarios restaurante={}", id);
        List<HorarioDisponibleDTO> response = restauranteService.listarHorariosDisponibles(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Horarios obtenidos correctamente"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<RestauranteDashboardDTO>> obtenerDashboard() {
        log.info("event=api_obtener_dashboard_restaurante");
        RestauranteDashboardDTO response = restauranteService.obtenerResumenDashboard();
        return ResponseEntity.ok(ApiResponse.success(response, "Resumen del dashboard obtenido correctamente"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/reportes")
    public ResponseEntity<ApiResponse<RestauranteReporteDTO>> obtenerReportes(
            @RequestParam(defaultValue = "semana") String periodo
    ) {
        log.info("event=api_obtener_reportes_restaurante periodo={}", periodo);
        RestauranteReporteDTO response = restauranteService.obtenerReportesRestaurante(periodo);
        return ResponseEntity.ok(ApiResponse.success(response, "Reportes obtenidos"));
    }

    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<List<StaffResponseDTO>>> listarStaff() {
        log.info("event=api_obtener_staff_restaurante");
        List<StaffResponseDTO> response = restauranteService.listarStaffRestaurante();
        return ResponseEntity.ok(ApiResponse.success(response, "Staff listado correctamente"));
    }
}