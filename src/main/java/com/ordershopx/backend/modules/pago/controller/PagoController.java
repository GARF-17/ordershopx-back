package com.ordershopx.backend.modules.pago.controller;

import com.ordershopx.backend.modules.pago.dto.request.PagoRequestDTO;
import com.ordershopx.backend.modules.pago.dto.response.PagoResponseDTO;
import com.ordershopx.backend.modules.pago.dto.response.ResumenPagoPedidoDTO;
import com.ordershopx.backend.modules.pago.service.IPagoService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Slf4j
public class PagoController {

    private final IPagoService pagoService;

    // REGISTRAR PAGO
    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> registrarPago(
            @Valid @RequestBody PagoRequestDTO request
    ) {

        log.info(
                "event=api_registrar_pago pedido={} metodo={} monto={}",
                request.getIdPedido(),
                request.getMetodoPago(),
                request.getMonto()
        );

        PagoResponseDTO response = pagoService.registrarPago(request);

        return ResponseEntity.status(201)
                .body(
                        ApiResponse.created(
                                response,
                                "Pago registrado correctamente"
                        )
                );
    }

    // OBTENER PAGO POR ID
    @GetMapping("/{idPago}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> obtenerPagoPorId(
            @PathVariable UUID idPago
    ) {

        log.info("event=api_obtener_pago id={}", idPago);

        PagoResponseDTO response = pagoService.obtenerPagoPorId(idPago);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Pago obtenido correctamente"
                )
        );
    }

    // LISTAR PAGOS DE UN PEDIDO
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listarPagosPedido(
            @PathVariable UUID idPedido
    ) {

        log.info("event=api_listar_pagos_pedido pedido={}", idPedido);

        List<PagoResponseDTO> response =
                pagoService.listarPagosPedido(idPedido);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Pagos obtenidos correctamente"
                )
        );
    }

    // RESUMEN DE PAGOS DEL PEDIDO
    @GetMapping("/pedido/{idPedido}/resumen")
    public ResponseEntity<ApiResponse<ResumenPagoPedidoDTO>> obtenerResumenPago(
            @PathVariable UUID idPedido
    ) {

        log.info("event=api_resumen_pago pedido={}", idPedido);

        ResumenPagoPedidoDTO response =
                pagoService.obtenerResumenPago(idPedido);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Resumen de pago obtenido correctamente"
                )
        );
    }

    // LISTAR TODOS LOS PAGOS CONFIRMADOS
    @GetMapping("/confirmados")
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listarPagosConfirmados() {

        log.info("event=api_listar_pagos_confirmados");

        List<PagoResponseDTO> response =
                pagoService.listarPagosConfirmados();

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Pagos confirmados obtenidos correctamente"
                )
        );
    }
}