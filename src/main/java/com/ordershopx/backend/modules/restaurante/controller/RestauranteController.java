package com.ordershopx.backend.modules.restaurante.controller;

import com.ordershopx.backend.modules.restaurante.dto.request.RestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.request.UbicacionRestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/restaurantes")
@RequiredArgsConstructor
@Slf4j
public class RestauranteController {

    private final IRestauranteService restauranteService;

    // OBTENER MI RESTAURANTE
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

    // 📍 UBICACIÓN
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
}