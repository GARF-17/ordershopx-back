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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('COMENSAL')")
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
    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE')")
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
    @PreAuthorize("hasAuthority('COMENSAL')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> listarMisPedidos() {

        log.info("event=api_listar_mis_pedidos");

        List<PedidoResponseDTO> response = pedidoService.listarPedidosCliente();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Pedidos obtenidos correctamente")
        );
    }

    // LISTAR TODOS LOS PEDIDOS DEL RESTAURANTE
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/restaurante")
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> listarPedidosRestaurante() {

        log.info("event=api_listar_pedidos_restaurante");

        List<PedidoResponseDTO> response = pedidoService.listarPedidosRestaurante();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Pedidos del restaurante obtenidos correctamente")
        );
    }

    // LISTAR COLA DEL RESTAURANTE
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/restaurante/cola")
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> listarColaRestaurante() {
        log.info("event=api_listar_cola_restaurante");
        List<PedidoResponseDTO> response = pedidoService.listarColaRestaurante();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Cola obtenida correctamente")
        );
    }

    // CAMBIAR ESTADO DEL PEDIDO
    // ======================================================================================
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
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

    // VALIDAR CÓDIGO DE RECOJO (Solo Staff - Cuando el cliente llega a recoger)
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PostMapping("/validar-codigo/{codigo}")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> validarCodigoRecojo(@PathVariable String codigo) {

        log.info("event=api_validar_codigo codigo={}", codigo);

        PedidoResponseDTO response = pedidoService.validarCodigoRecojo(codigo.toUpperCase());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Pedido entregado correctamente")
        );
    }
}