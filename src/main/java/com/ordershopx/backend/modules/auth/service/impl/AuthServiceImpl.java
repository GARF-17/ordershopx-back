package com.ordershopx.backend.modules.auth.service.impl;

import com.ordershopx.backend.modules.auth.dto.request.LoginRequestDTO;
import com.ordershopx.backend.modules.auth.dto.request.RegisterRequestDTO;
import com.ordershopx.backend.modules.auth.dto.response.LoginResponseDTO;
import com.ordershopx.backend.modules.auth.dto.response.RegisterResponseDTO;
import com.ordershopx.backend.modules.auth.service.IAuthService;

import com.ordershopx.backend.modules.cliente.service.IClienteService;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;

import com.ordershopx.backend.shared.enums.RolGlobal;
import com.ordershopx.backend.shared.exception.BadRequestException;
import com.ordershopx.backend.shared.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioService usuarioService;
    private final IClienteService clienteService;
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // LOGIN
    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {

        log.info("event=auth_login_start correo={}", request.getCorreoElectronico());

        Usuario usuario = usuarioService.obtenerPorCorreo(request.getCorreoElectronico());

        if (!usuario.getEstaActivo()) {
            throw new BadRequestException("El usuario está desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getClaveHash())) {
            log.warn("event=auth_login_fail reason=bad_credentials correo={}", request.getCorreoElectronico());
            throw new BadRequestException("Credenciales inválidas");
        }

        log.info("event=auth_login_success usuarioId={}", usuario.getUsuarioId());

        usuario.setFechaUltimoLogin(OffsetDateTime.now());
        usuarioRepository.save(usuario);

        UUID idRestaurante = null;
        if (usuario.getRol() == RolGlobal.STAFF_RESTAURANTE) {
            idRestaurante = usuarioRestauranteRepository.findByUsuarioUsuarioId(usuario.getUsuarioId())
                    .stream()
                    .filter(UsuarioRestaurante::getEstaActivo) // Solo restaurantes donde siga trabajando
                    .findFirst()
                    .map(ur -> ur.getRestaurante().getIdUsuario())
                    .orElse(null);
        }

        return LoginResponseDTO.builder()
                .token(jwtService.generateToken(usuario))
                .correoElectronico(usuario.getCorreoElectronico())
                .rol(usuario.getRol().name())
                .idRestaurante(idRestaurante)
                .idUsuario(usuario.getUsuarioId())
                .build();
    }

    // REGISTER PÚBLICO
    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {

        log.info("event=auth_register_start correo={}", request.getCorreoElectronico());

        RolGlobal rol = parseRol(request.getRol());

        Usuario usuario = Usuario.builder()
                .correoElectronico(request.getCorreoElectronico())
                .dni(request.getDni())
                .claveHash(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .rol(rol)
                .estaActivo(true)
                .build();

        usuario = usuarioService.crearUsuario(usuario);

        crearPerfilSegunRol(request, usuario, rol);

        log.info("event=auth_register_success usuarioId={}", usuario.getUsuarioId());

        return RegisterResponseDTO.builder()
                .correoElectronico(usuario.getCorreoElectronico())
                .mensaje("Usuario registrado correctamente")
                .build();
    }

    // CREACIÓN DE PERFIL
    private void crearPerfilSegunRol(RegisterRequestDTO request, Usuario usuario, RolGlobal rol) {

        if (rol == RolGlobal.COMENSAL) {
            clienteService.crearDesdeRegister(
                    usuario,
                    request.getNombre(),
                    request.getApellido()
            );

            log.info("event=cliente_creado usuarioId={}", usuario.getUsuarioId());

        } else {
            throw new BadRequestException("Operación denegada: Los restaurantes deben registrarse por el flujo de Onboarding.");
        }
    }

    // UTILIDADES
    private RolGlobal parseRol(String rol) {
        try {
            return RolGlobal.valueOf(rol);
        } catch (Exception e) {
            throw new BadRequestException("Rol inválido");
        }
    }
}