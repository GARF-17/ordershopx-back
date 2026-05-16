package com.ordershopx.backend.modules.pedido.controller;

import com.ordershopx.backend.modules.pedido.dto.request.CambiarEstadoPedidoDTO;
import com.ordershopx.backend.modules.pedido.dto.request.PedidoRequestDTO;
import com.ordershopx.backend.modules.pedido.dto.response.PedidoResponseDTO;
import com.ordershopx.backend.modules.pedido.service.IPedidoService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Slf4j
public class PedidoController {

    private final IPedidoService pedidoService;

    // CREAR PEDIDO
    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> crearPedido(
            @Valid @RequestBody PedidoRequestDTO request
    ) {

        log.info("event=api_crear_pedido restaurante={}", request.getIdRestaurante());

        PedidoResponseDTO response = pedidoService.crearPedido(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(response, "Pedido creado correctamente"));
    }

    // OBTENER PEDIDO POR ID
    @GetMapping("/{idPedido}")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> obtenerPedido(
            @PathVariable UUID idPedido,
            @RequestParam(defaultValue = "false") boolean incluirHistorial
    ) {

        log.info("event=api_obtener_pedido id={} historial={}", idPedido, incluirHistorial);

        PedidoResponseDTO response = pedidoService.obtenerPedido(idPedido, incluirHistorial);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Pedido obtenido correctamente")
        );
    }

    // LISTAR MIS PEDIDOS
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> listarMisPedidos() {

        log.info("event=api_listar_mis_pedidos");

        List<PedidoResponseDTO> response = pedidoService.listarPedidosCliente();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Pedidos obtenidos correctamente")
        );
    }

    // LISTAR COLA DEL RESTAURANTE
    @GetMapping("/restaurante/cola")
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> listarColaRestaurante() {

        log.info("event=api_listar_cola_restaurante");

        List<PedidoResponseDTO> response = pedidoService.listarColaRestaurante();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Cola obtenida correctamente")
        );
    }

    // CAMBIAR ESTADO
    @PatchMapping("/estado")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> cambiarEstado(
            @Valid @RequestBody CambiarEstadoPedidoDTO request
    ) {

        log.info("event=api_cambiar_estado pedido={} estado={}",
                request.getIdPedido(), request.getEstado());

        PedidoResponseDTO response = pedidoService.cambiarEstado(request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Estado actualizado correctamente")
        );
    }

    @PostMapping("/validar-codigo/{codigo}")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> validarCodigoRecojo(@PathVariable String codigo) {

        log.info( "event=api_validar_codigo codigo={}", codigo);

        PedidoResponseDTO response =
                pedidoService.validarCodigoRecojo(
                        codigo.toUpperCase()
                );

        return ResponseEntity.ok( ApiResponse.success( response,"Pedido entregado correctamente")
        );

    }

}