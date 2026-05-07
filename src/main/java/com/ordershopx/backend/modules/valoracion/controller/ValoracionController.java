package com.ordershopx.backend.modules.valoracion.controller;

import com.ordershopx.backend.modules.valoracion.dto.request.ValoracionRequestDTO;
import com.ordershopx.backend.modules.valoracion.dto.response.ValoracionResponseDTO;
import com.ordershopx.backend.modules.valoracion.service.IValoracionService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/valoraciones")
@RequiredArgsConstructor
@Slf4j
public class ValoracionController {

    private final IValoracionService valoracionService;

    // REGISTRAR VALORACION
    @PostMapping
    public ResponseEntity<ApiResponse<ValoracionResponseDTO>> registrarValoracion(
            @Valid @RequestBody ValoracionRequestDTO request
    ) {

        log.info(
                "event=api_registrar_valoracion pedido={} puntuacion={}",
                request.getIdPedido(),
                request.getPuntuacion()
        );

        ValoracionResponseDTO response =
                valoracionService.registrarValoracion(request);

        return ResponseEntity.status(201)
                .body(
                        ApiResponse.created(
                                response,
                                "Valoración registrada correctamente"
                        )
                );
    }

    // OBTENER VALORACION POR ID
    @GetMapping("/{idValoracion}")
    public ResponseEntity<ApiResponse<ValoracionResponseDTO>> obtenerValoracionPorId(
            @PathVariable UUID idValoracion
    ) {

        log.info(
                "event=api_obtener_valoracion id={}",
                idValoracion
        );

        ValoracionResponseDTO response =
                valoracionService.obtenerValoracionPorId(idValoracion);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Valoración obtenida correctamente"
                )
        );
    }

    // OBTENER VALORACION POR PEDIDO
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<ApiResponse<ValoracionResponseDTO>> obtenerValoracionPorPedido(
            @PathVariable UUID idPedido
    ) {

        log.info(
                "event=api_obtener_valoracion_pedido pedido={}",
                idPedido
        );

        ValoracionResponseDTO response =
                valoracionService.obtenerValoracionPorPedido(idPedido);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Valoración obtenida correctamente"
                )
        );
    }

    // LISTAR VALORACIONES DEL RESTAURANTE
    @GetMapping("/restaurante/{idRestaurante}")
    public ResponseEntity<ApiResponse<List<ValoracionResponseDTO>>> listarValoracionesRestaurante(
            @PathVariable UUID idRestaurante
    ) {

        log.info(
                "event=api_listar_valoraciones_restaurante restaurante={}",
                idRestaurante
        );

        List<ValoracionResponseDTO> response =
                valoracionService.listarValoracionesRestaurante(idRestaurante);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Valoraciones obtenidas correctamente"
                )
        );
    }

    // LISTAR MIS VALORACIONES
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ValoracionResponseDTO>>> listarMisValoraciones() {

        log.info("event=api_listar_mis_valoraciones");

        List<ValoracionResponseDTO> response =
                valoracionService.listarMisValoraciones();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Valoraciones obtenidas correctamente"
                )
        );
    }

    // ELIMINAR VALORACION
    @DeleteMapping("/{idValoracion}")
    public ResponseEntity<ApiResponse<Void>> eliminarValoracion(
            @PathVariable UUID idValoracion
    ) {

        log.info(
                "event=api_eliminar_valoracion id={}",
                idValoracion
        );

        valoracionService.eliminarValoracion(idValoracion);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Valoración eliminada correctamente"
                )
        );
    }
}