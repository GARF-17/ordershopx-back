package com.ordershopx.backend.modules.auth.service.impl;

import com.ordershopx.backend.modules.auth.dto.request.LoginRequestDTO;
import com.ordershopx.backend.modules.auth.dto.request.RegisterRequestDTO;
import com.ordershopx.backend.modules.auth.dto.response.LoginResponseDTO;
import com.ordershopx.backend.modules.auth.dto.response.RegisterResponseDTO;
import com.ordershopx.backend.modules.auth.service.IAuthService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.cliente.repository.ClienteRepository;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.TipoRol;
import com.ordershopx.backend.shared.exception.BadRequestException;
import com.ordershopx.backend.shared.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final RestauranteRepository restauranteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {

        log.info("event=auth_login_start correo={}", request.getCorreoElectronico());
        Usuario usuario = usuarioService.obtenerPorCorreo(request.getCorreoElectronico());

        if (!usuario.getEstaActivo()) throw new BadRequestException("El usuario está desactivado");

        if (!passwordEncoder.matches(request.getPassword(), usuario.getClaveHash())) {
            log.warn("event=auth_login_fail reason=bad_credentials correo={}", request.getCorreoElectronico());
            throw new BadRequestException("Credenciales inválidas");
        }

        return LoginResponseDTO.builder()
                .token(jwtService.generateToken(usuario))
                .correoElectronico(usuario.getCorreoElectronico())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        log.info("event=auth_register_start correo={}", request.getCorreoElectronico());

        TipoRol rol = parseRol(request.getRol());
        validarCamposPorRol(request, rol);

        // Crear
        Usuario usuario = Usuario.builder()
                .correoElectronico(request.getCorreoElectronico())
                .dni(request.getDni())
                .claveHash(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .rol(rol)
                .estaActivo(true)
                .build();

        usuario = usuarioService.crearUsuario(usuario);

        // Crear perfil
        crearPerfilSegunRol(request, usuario, rol);

        log.info("event=auth_register_success usuarioId={}", usuario.getUsuarioId());
        return RegisterResponseDTO.builder()
                .correoElectronico(usuario.getCorreoElectronico())
                .mensaje("Usuario registrado correctamente")
                .build();
    }

    private TipoRol parseRol(String rol) {
        try {
            return TipoRol.valueOf(rol);
        } catch (Exception e) {
            throw new BadRequestException("Rol inválido");
        }
    }

    private void validarCamposPorRol(RegisterRequestDTO request, TipoRol rol) {

        if (rol == TipoRol.COMENSAL) {

            if (isBlank(request.getNombre())) {
                throw new BadRequestException("El nombre es obligatorio para COMENSAL");
            }

            if (isBlank(request.getApellido())) {
                throw new BadRequestException("El apellido es obligatorio para COMENSAL");
            }

        } else if (rol == TipoRol.RESTAURANTE) {

            if (isBlank(request.getNombreComercial())) {
                throw new BadRequestException("El nombre comercial es obligatorio");
            }
        }
    }

    private void crearPerfilSegunRol(RegisterRequestDTO request, Usuario usuario, TipoRol rol) {

        if (rol == TipoRol.COMENSAL) {

            Cliente cliente = Cliente.builder()
                    .usuario(usuario)
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .build();


            clienteRepository.save(cliente);

        } else if (rol == TipoRol.RESTAURANTE) {

            Restaurante restaurante = Restaurante.builder()
                    .usuario(usuario)
                    .nombreComercial(request.getNombreComercial())
                    .razonSocial(request.getRazonSocial())
                    .ruc(request.getRuc())
                    .direccionFiscal(request.getDireccionFiscal())
                    .build();

            restauranteRepository.save(restaurante);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}