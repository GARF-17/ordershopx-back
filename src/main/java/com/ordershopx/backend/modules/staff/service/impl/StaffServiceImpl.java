package com.ordershopx.backend.modules.staff.service.impl;

import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.staff.dto.request.ActualizarStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.CrearInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.ValidarInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.response.InvitacionStaffResponseDTO;
import com.ordershopx.backend.modules.staff.dto.response.UsuarioRestauranteResponseDTO;
import com.ordershopx.backend.modules.staff.entity.InvitacionStaff;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.mapper.InvitacionStaffMapper;
import com.ordershopx.backend.modules.staff.mapper.UsuarioRestauranteMapper;
import com.ordershopx.backend.modules.staff.repository.InvitacionStaffRepository;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.staff.service.IStaffService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.shared.enums.RolGlobal;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import com.ordershopx.backend.shared.exception.BadRequestException;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;
import com.ordershopx.backend.shared.security.jwt.JwtService;
import com.ordershopx.backend.shared.mail.IEmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements IStaffService {

    private final UsuarioRestauranteRepository usuarioRestauranteRepository;
    private final InvitacionStaffRepository invitacionStaffRepository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRestauranteMapper usuarioRestauranteMapper;
    private final InvitacionStaffMapper invitacionStaffMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioRestauranteResponseDTO> listarStaffActivo(UUID idRestaurante) {
        List<UsuarioRestaurante> staff = usuarioRestauranteRepository
                .findByRestauranteIdUsuarioAndEstaActivoTrue(idRestaurante);
        return usuarioRestauranteMapper.toResponseList(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvitacionStaffResponseDTO> listarInvitacionesPendientes(UUID idRestaurante) {
        List<InvitacionStaff> invitaciones = invitacionStaffRepository
                .findByRestauranteIdUsuarioAndAceptadaFalse(idRestaurante);
        return invitacionStaffMapper.toResponseList(invitaciones);
    }

    @Override
    @Transactional
    public void invitarEmpleado(CrearInvitacionStaffRequestDTO dto, UUID idOwner) {
        log.info("Verificando permisos del owner {} para invitar en restaurante {}", idOwner, dto.getIdRestaurante());
        validarPermisoOwner(idOwner, dto.getIdRestaurante());

        Restaurante restaurante = restauranteRepository.findById(dto.getIdRestaurante())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        if (invitacionStaffRepository.existsByRestauranteIdUsuarioAndCorreoAndAceptadaFalse(
                restaurante.getIdUsuario(), dto.getCorreo())) {
            throw new ConflictException("Ya existe una invitación pendiente para este correo en este restaurante.");
        }

        String pinSeguro = String.format("%06d", SECURE_RANDOM.nextInt(999999));
        String tokenJwt = jwtService.generateInvitationToken(dto.getCorreo());

        InvitacionStaff nuevaInvitacion = InvitacionStaff.builder()
                .restaurante(restaurante)
                .correo(dto.getCorreo())
                .rol(dto.getRol())
                .token(tokenJwt)
                .pin(pinSeguro)
                .expiraEn(OffsetDateTime.now().plusHours(48))
                .aceptada(false)
                .build();

        invitacionStaffRepository.save(nuevaInvitacion);

        // ENVIAMOS EL CORREO ELECTRÓNICO AL EMPLEADO INVITADO
        emailService.enviarCorreoInvitacion(
                dto.getCorreo(),
                tokenJwt,
                pinSeguro,
                dto.getRol().name()
        );

        log.info("Invitación creada. PIN: {} Token generado para {}", pinSeguro, dto.getCorreo());
    }

    @Override
    @Transactional
    public void validarInvitacion(ValidarInvitacionStaffRequestDTO dto) {
        log.info("Iniciando validación de invitación de staff");

        if (!jwtService.isInvitationToken(dto.getToken())) {
            throw new UnauthorizedException("El token proporcionado es inválido o ha expirado.");
        }

        InvitacionStaff invitacion = invitacionStaffRepository.findByTokenAndPinAndAceptadaFalse(dto.getToken(), dto.getPin())
                .orElseThrow(() -> new BadRequestException("PIN incorrecto o la invitación ya fue utilizada/cancelada."));

        if (invitacion.getExpiraEn().isBefore(OffsetDateTime.now())) {
            throw new ConflictException("Esta invitación ha caducado. Solicite una nueva a su empleador.");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .correoElectronico(invitacion.getCorreo())
                .claveHash(passwordEncoder.encode(dto.getNuevaContrasena()))
                .dni(dto.getDni())
                .telefono(dto.getTelefono())
                .rol(RolGlobal.STAFF_RESTAURANTE)
                .estaActivo(true)
                .build();
        usuarioRepository.save(nuevoUsuario);

        UsuarioRestaurante asignacion = UsuarioRestaurante.builder()
                .usuario(nuevoUsuario)
                .restaurante(invitacion.getRestaurante())
                .rol(invitacion.getRol())
                .esPrincipal(false)
                .estaActivo(true)
                .build();
        usuarioRestauranteRepository.save(asignacion);

        invitacion.setAceptada(true);
        invitacionStaffRepository.save(invitacion);

        log.info("Empleado {} registrado exitosamente con el rol {}", nuevoUsuario.getCorreoElectronico(), invitacion.getRol());
    }

    @Override
    @Transactional
    public void actualizarEmpleado(UUID idRestaurante, UUID idUsuarioTarget, ActualizarStaffRequestDTO dto, UUID idOwner) {
        log.info("Owner {} intenta actualizar al usuario {} en restaurante {}", idOwner, idUsuarioTarget, idRestaurante);
        validarPermisoOwner(idOwner, idRestaurante);

        if (idOwner.equals(idUsuarioTarget)) {
            throw new ConflictException("No puedes modificar tu propia asignación principal desde este panel.");
        }

        UsuarioRestaurante asignacion = usuarioRestauranteRepository
                .findByUsuarioUsuarioIdAndRestauranteIdUsuario(idUsuarioTarget, idRestaurante)
                .orElseThrow(() -> new ResourceNotFoundException("El empleado no pertenece a este restaurante."));

        usuarioRestauranteMapper.updateEntity(asignacion, dto);

        usuarioRestauranteRepository.save(asignacion);
        log.info("Asignación de staff actualizada exitosamente.");
    }

    private void validarPermisoOwner(UUID idUsuario, UUID idRestaurante) {
        UsuarioRestaurante relacion = usuarioRestauranteRepository
                .findByUsuarioUsuarioIdAndRestauranteIdUsuario(idUsuario, idRestaurante)
                .orElseThrow(() -> new UnauthorizedException("No tienes acceso a este restaurante."));

        if (!relacion.getRol().equals(RolRestaurante.OWNER) && !relacion.getRol().equals(RolRestaurante.ADMIN_LOCAL)) {
            throw new UnauthorizedException("No tienes permisos suficientes (Solo OWNER o ADMIN_LOCAL).");
        }
        if (!relacion.getEstaActivo()) {
            throw new UnauthorizedException("Tu cuenta se encuentra inactiva en este restaurante.");
        }
    }
}