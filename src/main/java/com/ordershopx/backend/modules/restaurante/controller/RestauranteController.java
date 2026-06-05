package com.ordershopx.backend.modules.restaurante.controller;

import com.ordershopx.backend.modules.restaurante.dto.request.RestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.request.UbicacionRestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.HorarioDisponibleDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
@Slf4j
public class RestauranteController {

    private final IRestauranteService restauranteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> listarRestaurantes() {

        log.info("event=api_listar_restaurantes");

        List<RestauranteResponseDTO> response =
                restauranteService.listarRestaurantes();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Restaurantes obtenidos correctamente")
        );
    }

    @GetMapping("/cercanos")
    public ResponseEntity<ApiResponse<List<RestauranteResponseDTO>>> buscarRestaurantesCercanos(
            @RequestParam("lat") Double lat,
            @RequestParam("lng") Double lng,
            // 🔥 AQUÍ ESTÁ EL CAMBIO A 1.5 KILÓMETROS
            @RequestParam(value = "radio", defaultValue = "1.5") Double radio
    ) {

        log.info("event=api_buscar_restaurantes_cercanos lat={} lng={} radio={}", lat, lng, radio);

        List<RestauranteResponseDTO> response =
                restauranteService.buscarRestaurantesCercanos(lat, lng, radio);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Restaurantes cercanos obtenidos correctamente")
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> obtenerMiRestaurante() {

        log.info("event=api_obtener_mi_restaurante");

        RestauranteResponseDTO response = restauranteService.obtenerMiRestaurante();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Restaurante obtenido correctamente")
        );
    }

    // ACTUALIZAR DATOS
    @PutMapping
    public ResponseEntity<ApiResponse<RestauranteResponseDTO>> actualizarRestaurante(
            @RequestBody RestauranteRequestDTO request
    ) {

        log.info("event=api_actualizar_restaurante");

        RestauranteResponseDTO response = restauranteService.actualizarRestaurante(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Restaurante actualizado correctamente")
        );
    }

    // UBICACIÓN
    @PutMapping("/ubicacion")
    public ResponseEntity<ApiResponse<Void>> actualizarUbicacion(
            @RequestBody UbicacionRestauranteRequestDTO request
    ) {

        log.info("event=api_actualizar_ubicacion_restaurante");

        restauranteService.actualizarUbicacion(request);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Ubicación actualizada correctamente")
        );
    }

    //  ESTADO
    @PutMapping("/estado")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(
            @RequestParam String estado
    ) {

        log.info("event=api_cambiar_estado estado={}", estado);

        restauranteService.cambiarEstado(estado);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Estado actualizado correctamente")
        );
    }


    @GetMapping("/{id}/horarios")
    public ResponseEntity<List<HorarioDisponibleDTO>>
    listarHorariosDisponibles(
            @PathVariable UUID id
    ) {

        return ResponseEntity.ok(
                restauranteService.listarHorariosDisponibles(id)
        );
    }
}