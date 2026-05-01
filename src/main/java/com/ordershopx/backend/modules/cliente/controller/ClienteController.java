package com.ordershopx.backend.modules.cliente.controller;

import com.ordershopx.backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.request.UbicacionRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.ordershopx.backend.modules.cliente.service.IClienteService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClienteController {

    private final IClienteService clienteService;

    // CREAR CLIENTE
    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> crearCliente(
            @Valid @RequestBody ClienteRequestDTO request
    ) {

        log.info("event=api_crear_cliente");

        ClienteResponseDTO response = clienteService.crearCliente(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(response, "Cliente creado correctamente"));
    }

    // OBTENER MI CLIENTE
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> obtenerMiCliente() {

        log.info("event=api_obtener_mi_cliente");

        ClienteResponseDTO response = clienteService.obtenerMiCliente();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Cliente obtenido correctamente")
        );
    }

    //  ACTUALIZAR UBICACIÓN
    @PutMapping("/ubicacion")
    public ResponseEntity<ApiResponse<Void>> actualizarUbicacion(
            @Valid @RequestBody UbicacionRequestDTO request
    ) {

        log.info("event=api_actualizar_ubicacion");

        clienteService.actualizarUbicacion(request);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Ubicación actualizada correctamente")
        );
    }
}