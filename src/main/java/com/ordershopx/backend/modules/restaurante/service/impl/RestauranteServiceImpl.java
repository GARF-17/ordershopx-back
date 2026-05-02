package com.ordershopx.backend.modules.restaurante.service.impl;

import com.ordershopx.backend.modules.restaurante.dto.request.RestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.request.UbicacionRestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.mapper.RestauranteMapper;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.EstadoRestaurante;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestauranteServiceImpl implements IRestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final RestauranteMapper restauranteMapper;
    private final IUsuarioService usuarioService;

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    // OBTENER MI RESTAURANTE
    @Override
    @Transactional(readOnly = true)
    public RestauranteResponseDTO obtenerMiRestaurante() {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=obtener_restaurante_start usuario={}", usuario.getCorreoElectronico());

        Restaurante restaurante = restauranteRepository.findById(usuario.getUsuarioId())
                .orElseThrow(() -> {
                    log.warn("event=restaurante_not_found usuario={}", usuario.getCorreoElectronico());
                    return new ResourceNotFoundException("Restaurante no encontrado");
                });

        log.info("event=obtener_restaurante_success usuario={}", usuario.getCorreoElectronico());

        return restauranteMapper.toResponse(restaurante);
    }

    // ACTUALIZAR RESTAURANTE
    @Override
    @Transactional
    public RestauranteResponseDTO actualizarRestaurante(RestauranteRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=actualizar_restaurante_start usuario={}", usuario.getCorreoElectronico());

        Restaurante restaurante = restauranteRepository.findById(usuario.getUsuarioId())
                .orElseThrow(() -> {
                    log.warn("event=restaurante_not_found usuario={}", usuario.getCorreoElectronico());
                    return new ResourceNotFoundException("Restaurante no encontrado");
                });

        restaurante.setNombreComercial(request.getNombreComercial());
        restaurante.setRazonSocial(request.getRazonSocial());
        restaurante.setDireccionFiscal(request.getDireccionFiscal());
        restaurante.setTiempoPreparacionMin(request.getTiempoPreparacionMin());
        restaurante.setTiempoPreparacionMax(request.getTiempoPreparacionMax());
        restaurante.setCapacidadCocina(request.getCapacidadCocina());

        restauranteRepository.save(restaurante);

        log.info("event=actualizar_restaurante_success usuario={}", usuario.getCorreoElectronico());

        return restauranteMapper.toResponse(restaurante);
    }

    // ACTUALIZAR UBICACIÓN
    @Override
    @Transactional
    public void actualizarUbicacion(UbicacionRestauranteRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=actualizar_ubicacion_restaurante_start usuario={}", usuario.getCorreoElectronico());

        Restaurante restaurante = restauranteRepository.findById(usuario.getUsuarioId())
                .orElseThrow(() -> {
                    log.warn("event=restaurante_not_found usuario={}", usuario.getCorreoElectronico());
                    return new ResourceNotFoundException("Restaurante no encontrado");
                });

        restaurante.setLatitud(request.getLatitud());
        restaurante.setLongitud(request.getLongitud());

        restauranteRepository.save(restaurante);

        log.info("event=actualizar_ubicacion_restaurante_success usuario={}", usuario.getCorreoElectronico());
    }

    // CAMBIAR ESTADO
    @Override
    @Transactional
    public void cambiarEstado(String estado) {

        Usuario usuario = getUsuarioAutenticado();

        log.info("event=cambiar_estado_start usuario={} estado={}",
                usuario.getCorreoElectronico(), estado);

        Restaurante restaurante = restauranteRepository.findById(usuario.getUsuarioId())
                .orElseThrow(() -> {
                    log.warn("event=restaurante_not_found usuario={}", usuario.getCorreoElectronico());
                    return new ResourceNotFoundException("Restaurante no encontrado");
                });

        try {
            restaurante.setEstado(EstadoRestaurante.valueOf(estado));
        } catch (IllegalArgumentException e) {
            log.warn("event=estado_invalido estado={}", estado);
            throw new IllegalArgumentException("Estado inválido");
        }

        restauranteRepository.save(restaurante);

        log.info("event=cambiar_estado_success usuario={} estado={}",
                usuario.getCorreoElectronico(), estado);
    }

    // CREAR DESDE REGISTER
    @Override
    @Transactional
    public void crearDesdeRegister(Usuario usuario,
                                   String nombreComercial,
                                   String razonSocial,
                                   String ruc,
                                   String direccionFiscal) {

        log.info("event=crear_restaurante_register usuario={}", usuario.getCorreoElectronico());

        Restaurante restaurante = Restaurante.builder()
                .usuario(usuario)
                .nombreComercial(nombreComercial)
                .razonSocial(razonSocial)
                .ruc(ruc)
                .direccionFiscal(direccionFiscal)
                .estado(EstadoRestaurante.ABIERTO)
                .build();

        restauranteRepository.save(restaurante);

        log.info("event=crear_restaurante_register_success usuario={}", usuario.getCorreoElectronico());
    }
}