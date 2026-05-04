package com.ordershopx.backend.modules.categoria.controller;

import com.ordershopx.backend.modules.categoria.dto.request.CategoriaMenuRequestDTO;
import com.ordershopx.backend.modules.categoria.dto.response.CategoriaMenuResponseDTO;
import com.ordershopx.backend.modules.categoria.service.ICategoriaMenuService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriaMenuController {

    private final ICategoriaMenuService categoriaService;

    // CREAR CATEGORIA
    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaMenuResponseDTO>> crearCategoria(
            @Valid @RequestBody CategoriaMenuRequestDTO request
    ) {

        log.info("event=api_crear_categoria");

        CategoriaMenuResponseDTO response = categoriaService.crearCategoria(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(response, "Categoría creada correctamente"));
    }

    // LISTAR MIS CATEGORIAS
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<CategoriaMenuResponseDTO>>> listarMisCategorias() {

        log.info("event=api_listar_categorias");

        List<CategoriaMenuResponseDTO> response = categoriaService.listarMisCategorias();

        return ResponseEntity.ok(
                ApiResponse.success(response, "Categorías obtenidas correctamente")
        );
    }

    // ACTUALIZAR
    @PutMapping("/{idCategoria}")
    public ResponseEntity<ApiResponse<CategoriaMenuResponseDTO>> actualizarCategoria(
            @PathVariable UUID idCategoria,
            @Valid @RequestBody CategoriaMenuRequestDTO request
    ) {

        log.info("event=api_actualizar_categoria idCategoria={}", idCategoria);

        CategoriaMenuResponseDTO response =
                categoriaService.actualizarCategoria(idCategoria, request);

        return ResponseEntity.ok(
                ApiResponse.success(response, "Categoría actualizada correctamente")
        );
    }

    // ELIMINAR
    @DeleteMapping("/{idCategoria}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(
            @PathVariable UUID idCategoria
    ) {

        log.info("event=api_eliminar_categoria idCategoria={}", idCategoria);

        categoriaService.eliminarCategoria(idCategoria);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Categoría eliminada correctamente")
        );
    }
}