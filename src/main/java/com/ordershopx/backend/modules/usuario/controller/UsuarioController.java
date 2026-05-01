package com.ordershopx.backend.modules.usuario.controller;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping("/por-correo")
    public ResponseEntity<ApiResponse<Usuario>> obtenerPorCorreo(
            @RequestParam String correo) {

        log.info("event=api_get_usuario_by_correo correo={}", correo);

        Usuario usuario = usuarioService.obtenerPorCorreo(correo);

        return ResponseEntity.ok(
                ApiResponse.success(usuario, "Usuario encontrado correctamente")
        );
    }

    @GetMapping("/validar-disponibilidad")
    public ResponseEntity<ApiResponse<Void>> validarDisponibilidad(
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono) {

        log.info("event=api_validar_disponibilidad correo={} dni={} telefono={}",
                correo, dni, telefono);

        usuarioService.validarDisponibilidad(correo, dni, telefono);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Datos disponibles")
        );
    }
}