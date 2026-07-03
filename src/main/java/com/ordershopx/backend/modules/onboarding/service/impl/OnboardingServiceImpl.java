package com.ordershopx.backend.modules.onboarding.service.impl;

import com.ordershopx.backend.modules.onboarding.dto.request.SolicitudRestauranteRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.request.ValidarInvitacionRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.AprobacionResponseDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.SolicitudRestauranteResponseDTO;
import com.ordershopx.backend.modules.onboarding.entity.InvitacionRestaurante;
import com.ordershopx.backend.modules.onboarding.entity.SolicitudRestaurante;
import com.ordershopx.backend.modules.onboarding.mapper.SolicitudRestauranteMapper;
import com.ordershopx.backend.modules.onboarding.repository.InvitacionRestauranteRepository;
import com.ordershopx.backend.modules.onboarding.repository.SolicitudRestauranteRepository;
import com.ordershopx.backend.modules.onboarding.service.IOnboardingService;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.shared.enums.*;
import com.ordershopx.backend.shared.exception.ConflictException;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;
import com.ordershopx.backend.shared.exception.BadRequestException;
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
public class OnboardingServiceImpl implements IOnboardingService {

    private final SolicitudRestauranteRepository solicitudRepository;
    private final InvitacionRestauranteRepository invitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;
    private final SolicitudRestauranteMapper solicitudMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final IEmailService emailService;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    @Transactional
    public SolicitudRestauranteResponseDTO registrarSolicitud(SolicitudRestauranteRequestDTO dto) {
        log.info("Iniciando registro de nueva solicitud para el RUC: {}", dto.getRuc());

        if (solicitudRepository.existsByRuc(dto.getRuc())) {
            throw new ConflictException("Ya existe una solicitud en proceso para el RUC proporcionado.");
        }
        if (usuarioRepository.existsByDni(dto.getEncargadoDni())) {
            throw new ConflictException("El DNI " + dto.getEncargadoDni() + " ya se encuentra registrado en nuestro sistema.");
        }
        if (usuarioRepository.existsByCorreoElectronico(dto.getEncargadoCorreo())) {
            throw new ConflictException("El correo " + dto.getEncargadoCorreo() + " ya pertenece a una cuenta existente.");
        }

        SolicitudRestaurante nuevaSolicitud = solicitudMapper.toEntity(dto);
        nuevaSolicitud.setEstado(EstadoSolicitudRestaurante.PENDIENTE);
        nuevaSolicitud.setCantidadReenvios(0);

        SolicitudRestaurante solicitudGuardada = solicitudRepository.save(nuevaSolicitud);
        log.info("Solicitud creada exitosamente con ID: {}", solicitudGuardada.getIdSolicitud());

        return solicitudMapper.toResponse(solicitudGuardada);
    }

    @Transactional
    @Override
    public AprobacionResponseDTO aprobarSolicitud(UUID idSolicitud, String adminCorreo) {
        log.info("Administrador {} intentando aprobar la solicitud {}", adminCorreo, idSolicitud);

        SolicitudRestaurante solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada."));

        Usuario admin = usuarioRepository.findByCorreoElectronico(adminCorreo)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador no encontrado."));

        if (solicitud.getEstado() != EstadoSolicitudRestaurante.PENDIENTE &&
                solicitud.getEstado() != EstadoSolicitudRestaurante.EN_REVISION) {
            throw new ConflictException("La solicitud no está en un estado válido para ser aprobada.");
        }

        solicitud.setEstado(EstadoSolicitudRestaurante.APROBADA);
        solicitud.setAprobadoPor(admin);
        solicitud.setFechaRevision(OffsetDateTime.now());
        solicitudRepository.save(solicitud);

        String pinSeguro = generarPinSeguro();
        String tokenJwt = jwtService.generateInvitationToken(solicitud.getEncargadoCorreo());

        InvitacionRestaurante invitacion = InvitacionRestaurante.builder()
                .solicitud(solicitud)
                .token(tokenJwt)
                .pin(pinSeguro)
                .expiraEn(OffsetDateTime.now().plusHours(24))
                .estado(EstadoInvitacion.PENDIENTE)
                .build();

        invitacionRepository.save(invitacion);

        emailService.enviarCorreoInvitacion(
                solicitud.getEncargadoCorreo(),
                tokenJwt,
                pinSeguro,
                "DUEÑO DE RESTAURANTE (OWNER)"
        );

        log.info("Generado PIN y Token para la solicitud {}.", idSolicitud);

        return AprobacionResponseDTO.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .estado("APROBADA")
                .pinGenerado(pinSeguro)
                .tokenGenerado(tokenJwt)
                .mensaje("¡Copia este PIN y Token para el paso de validación!")
                .build();
    }

    @Override
    @Transactional
    public void validarInvitacion(ValidarInvitacionRequestDTO dto) {
        log.info("Iniciando validación de invitación con PIN");

        if (!jwtService.isInvitationToken(dto.getToken())) {
            throw new UnauthorizedException("El token proporcionado no es válido, no es de invitación o ha expirado.");
        }

        InvitacionRestaurante invitacion = invitacionRepository.findByTokenAndPin(dto.getToken(), dto.getPin())
                .orElseThrow(() -> new BadRequestException("El PIN ingresado es incorrecto o no coincide con el enlace."));

        if (invitacion.getEstado() != EstadoInvitacion.PENDIENTE) {
            throw new ConflictException("Esta invitación ya fue activada, expiró o fue cancelada.");
        }
        if (invitacion.getExpiraEn().isBefore(OffsetDateTime.now())) {
            invitacion.setEstado(EstadoInvitacion.EXPIRADA);
            invitacionRepository.save(invitacion);
            throw new ConflictException("El tiempo para aceptar esta invitación ha caducado.");
        }

        SolicitudRestaurante solicitud = invitacion.getSolicitud();

        boolean dniExiste    = usuarioRepository.existsByDni(solicitud.getEncargadoDni());
        boolean correoExiste = usuarioRepository.existsByCorreoElectronico(solicitud.getEncargadoCorreo());

        if (dniExiste || correoExiste) {
            invitacion.setEstado(EstadoInvitacion.CANCELADA);
            invitacionRepository.save(invitacion);
            solicitud.setEstado(EstadoSolicitudRestaurante.RECHAZADA);
            solicitudRepository.save(solicitud);
            String motivo = dniExiste ? "El DNI" : "El correo";
            throw new ConflictException("No se puede completar el registro: " + motivo + " ya pertenece a otra cuenta. Por seguridad, esta invitación ha sido CANCELADA.");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .correoElectronico(solicitud.getEncargadoCorreo())
                .tipoDocumento("DNI")
                .dni(solicitud.getEncargadoDni())
                .claveHash(passwordEncoder.encode(dto.getNuevaContrasena()))
                .telefono(solicitud.getEncargadoTelefono())
                .rol(RolGlobal.STAFF_RESTAURANTE)
                .estaActivo(true)
                .build();

        usuarioRepository.save(nuevoUsuario);

        Restaurante nuevoRestaurante = Restaurante.builder()
                .usuario(nuevoUsuario)
                .nombreComercial(solicitud.getNombreComercial())
                .razonSocial(solicitud.getRazonSocial())
                .ruc(solicitud.getRuc())
                .direccionFiscal(solicitud.getDireccionFiscal())
                .latitud(solicitud.getLatitud())
                .longitud(solicitud.getLongitud())
                .estado(EstadoRestaurante.ABIERTO)
                .onboardingCompletado(true)
                .creadoPor(solicitud.getAprobadoPor())
                .validadoPor(solicitud.getAprobadoPor())
                .validadoEn(OffsetDateTime.now())
                .build();

        restauranteRepository.save(nuevoRestaurante);

        UsuarioRestaurante asignacionOwner = UsuarioRestaurante.builder()
                .usuario(nuevoUsuario)
                .restaurante(nuevoRestaurante)
                .rol(RolRestaurante.OWNER)
                .esPrincipal(true)
                .estaActivo(true)
                .build();

        usuarioRestauranteRepository.save(asignacionOwner);

        invitacion.setEstado(EstadoInvitacion.ACTIVADA);
        invitacion.setUsadoEn(OffsetDateTime.now());
        invitacionRepository.save(invitacion);

        solicitud.setEstado(EstadoSolicitudRestaurante.ACTIVA);
        solicitudRepository.save(solicitud);

        log.info("Onboarding completado exitosamente para el RUC {}.", solicitud.getRuc());
    }

    @Override
    public SolicitudRestauranteResponseDTO consultarEstado(UUID solicitudId) {
        return null;
    }

    @Override
    @Transactional
    public void aprobarSolicitudYGenerarPin(UUID idSolicitud) {
        log.info("Aprobando solicitud {} y generando PIN...", idSolicitud);

        SolicitudRestaurante solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada."));

        if (solicitud.getEstado() != EstadoSolicitudRestaurante.PENDIENTE &&
                solicitud.getEstado() != EstadoSolicitudRestaurante.EN_REVISION) {
            throw new ConflictException("La solicitud no está en un estado válido para ser aprobada.");
        }

        solicitud.setEstado(EstadoSolicitudRestaurante.APROBADA);
        solicitud.setFechaRevision(OffsetDateTime.now());
        solicitudRepository.save(solicitud);

        String pinSeguro = generarPinSeguro();
        String tokenJwt  = jwtService.generateInvitationToken(solicitud.getEncargadoCorreo());

        InvitacionRestaurante invitacion = InvitacionRestaurante.builder()
                .solicitud(solicitud)
                .token(tokenJwt)
                .pin(pinSeguro)
                .expiraEn(OffsetDateTime.now().plusHours(24))
                .estado(EstadoInvitacion.PENDIENTE)
                .build();

        invitacionRepository.save(invitacion);

        emailService.enviarCorreoInvitacion(
                solicitud.getEncargadoCorreo(),
                tokenJwt,
                pinSeguro,
                "DUEÑO DE RESTAURANTE (OWNER)"
        );

        log.info("Proceso de aprobación completado para solicitud {}.", idSolicitud);
    }


    @Override
    @Transactional(readOnly = true)
    public List<SolicitudRestauranteResponseDTO> listarSolicitudes() {
        log.info("Listando todas las solicitudes de restaurantes");
        return solicitudRepository.findAll().stream()
                .map(solicitudMapper::toResponse)
                .toList();
    }

    private String generarPinSeguro() {
        int numero = SECURE_RANDOM.nextInt(999999);
        return String.format("%06d", numero);
    }
}