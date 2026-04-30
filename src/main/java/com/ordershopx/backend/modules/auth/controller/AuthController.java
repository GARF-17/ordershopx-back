package com.ordershopx.backend.modules.auth.controller;

import com.ordershopx.backend.modules.auth.dto.request.LoginRequestDTO;
import com.ordershopx.backend.modules.auth.dto.request.RegisterRequestDTO;
import com.ordershopx.backend.modules.auth.dto.response.LoginResponseDTO;
import com.ordershopx.backend.modules.auth.dto.response.RegisterResponseDTO;
import com.ordershopx.backend.modules.auth.service.IAuthService;
import com.ordershopx.backend.shared.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthService authService;

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        log.info("event=auth_login_request correo={}", request.getCorreoElectronico());

        LoginResponseDTO response = authService.login(request);

        log.info("event=auth_login_response correo={}", request.getCorreoElectronico());

        return ResponseEntity.ok(
                ApiResponse.success(response, "Login exitoso")
        );
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request) {

        log.info("event=auth_register_request correo={}", request.getCorreoElectronico());

        RegisterResponseDTO response = authService.register(request);

        log.info("event=auth_register_response correo={}", request.getCorreoElectronico());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Usuario registrado correctamente"));
    }
}