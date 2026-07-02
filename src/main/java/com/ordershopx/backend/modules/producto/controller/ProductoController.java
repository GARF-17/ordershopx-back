package com.ordershopx.backend.modules.producto.controller;

import com.ordershopx.backend.modules.producto.dto.request.ProductoRequestDTO;
import com.ordershopx.backend.modules.producto.dto.response.ProductoResponseDTO;
import com.ordershopx.backend.modules.producto.service.IProductoService;
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
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {

    private final IProductoService productoService;

    // CREAR PRODUCTO
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> crearProducto(
            @Valid @RequestBody ProductoRequestDTO request
    ) {

        log.info("event=api_crear_producto nombre={}", request.getNombre());
        ProductoResponseDTO response = productoService.crearProducto(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(response, "Producto creado correctamente"));
    }

    // LISTAR MIS PRODUCTOS
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listarMisProductos() {

        log.info("event=api_listar_productos");
        List<ProductoResponseDTO> response = productoService.listarMisProductos();
        return ResponseEntity.ok(
                ApiResponse.success(response, "Productos obtenidos correctamente")
        );
    }

    // LISTAR POR CATEGORÍA
    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE')")
    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listarPorCategoria(
            @PathVariable UUID idCategoria
    ) {

        log.info("event=api_listar_productos_categoria categoriaId={}", idCategoria);
        List<ProductoResponseDTO> response = productoService.listarPorCategoria(idCategoria);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Productos por categoría obtenidos")
        );
    }

    // LISTAR PRODUCTOS PARA CLIENTE
    @PreAuthorize("hasAnyAuthority('COMENSAL', 'STAFF_RESTAURANTE')")
    @GetMapping("/restaurante/{idRestaurante}")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listarProductosCliente(
            @PathVariable UUID idRestaurante
    ) {

        log.info("event=api_listar_productos_cliente restauranteId={}", idRestaurante);
        List<ProductoResponseDTO> response =
                productoService.listarProductosCliente(idRestaurante);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Productos obtenidos correctamente")
        );
    }

    // ACTUALIZAR PRODUCTO
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PutMapping("/{idProducto}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizarProducto(
            @PathVariable UUID idProducto,
            @Valid @RequestBody ProductoRequestDTO request
    ) {

        log.info("event=api_actualizar_producto productoId={}", idProducto);
        ProductoResponseDTO response = productoService.actualizarProducto(idProducto, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Producto actualizado correctamente")
        );
    }

    // ELIMINAR PRODUCTO (SOFT DELETE - Solo Personal del Restaurante)
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @DeleteMapping("/{idProducto}")
    public ResponseEntity<ApiResponse<Void>> eliminarProducto(
            @PathVariable UUID idProducto
    ) {

        log.info("event=api_eliminar_producto productoId={}", idProducto);
        productoService.eliminarProducto(idProducto);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Producto eliminado correctamente")
        );
    }

    // CAMBIAR DISPONIBILIDAD
    @PreAuthorize("hasAuthority('STAFF_RESTAURANTE')")
    @PatchMapping("/{idProducto}/disponibilidad")
    public ResponseEntity<ApiResponse<Void>> cambiarDisponibilidad(
            @PathVariable UUID idProducto,
            @RequestParam Boolean disponible
    ) {

        log.info("event=api_cambiar_disponibilidad productoId={} disponible={}",
                idProducto, disponible);

        productoService.cambiarDisponibilidad(idProducto, disponible);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Disponibilidad actualizada correctamente")
        );
    }
}