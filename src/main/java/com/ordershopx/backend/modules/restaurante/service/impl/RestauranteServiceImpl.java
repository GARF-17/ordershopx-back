package com.ordershopx.backend.modules.restaurante.service.impl;

import com.ordershopx.backend.modules.pedido.repository.PedidoRepository;
import com.ordershopx.backend.modules.restaurante.dto.request.*;
import com.ordershopx.backend.modules.restaurante.dto.response.*;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.mapper.RestauranteMapper;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.enums.EstadoRestaurante;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestauranteServiceImpl implements IRestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final RestauranteMapper restauranteMapper;
    private final IUsuarioService usuarioService;
    private final PedidoRepository pedidoRepository;

    private Usuario getUsuarioAutenticado() {
        String correo = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return usuarioService.obtenerPorCorreo(correo);
    }

    // LISTAR HORARIOS DISPONIBLES
    @Override
    @Transactional(readOnly = true)
    public List<HorarioDisponibleDTO> listarHorariosDisponibles(UUID idRestaurante) {

        Restaurante restaurante = restauranteRepository
                .findById(idRestaurante)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Restaurante no encontrado")
                );

        int tiempoMin = restaurante.getTiempoPreparacionMin() != null
                ? restaurante.getTiempoPreparacionMin()
                : 10;

        int capacidad = restaurante.getCapacidadCocina() != null
                ? restaurante.getCapacidadCocina()
                : 5;

        List<HorarioDisponibleDTO> horarios = new ArrayList<>();

        OffsetDateTime ahora = OffsetDateTime.now()
                .withSecond(0)
                .withNano(0)
                .plusMinutes(tiempoMin);

        OffsetDateTime inicio = ahora
                .withMinute((ahora.getMinute() / 10) * 10);

        for (int i = 0; i < 12; i++) {

            OffsetDateTime horario = inicio.plusMinutes(i * 10L)
                    .withSecond(0)
                    .withNano(0);

            OffsetDateTime inicioSlot = horario;
            OffsetDateTime finSlot = horario.plusMinutes(10);

            long pedidos = pedidoRepository.countByHorarioRango(
                    idRestaurante,
                    List.of(EstadoPedido.EN_COLA, EstadoPedido.PREPARANDO),
                    inicioSlot,
                    finSlot
            );

            boolean disponible = pedidos < capacidad;

            horarios.add(
                    HorarioDisponibleDTO.builder()
                            .hora(horario)
                            .cuposDisponibles(Math.max(0, capacidad - (int) pedidos))
                            .disponible(disponible)
                            .build()
            );
        }

        return horarios;
    }

    // OBTENER MI RESTAURANTE
    @Override
    public RestauranteResponseDTO obtenerMiRestaurante() {

        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = restauranteRepository
                .findById(usuario.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        return restauranteMapper.toResponse(restaurante);
    }

    // LISTAR RESTAURANTES
    @Override
    public List<RestauranteResponseDTO> listarRestaurantes() {
        return restauranteRepository.findAll()
                .stream()
                .map(restauranteMapper::toResponse)
                .toList();
    }

    // ACTUALIZAR RESTAURANTE
    @Override
    @Transactional
    public RestauranteResponseDTO actualizarRestaurante(RestauranteRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        Restaurante restaurante = restauranteRepository
                .findById(usuario.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        restaurante.setNombreComercial(request.getNombreComercial());
        restaurante.setRazonSocial(request.getRazonSocial());
        restaurante.setDireccionFiscal(request.getDireccionFiscal());
        restaurante.setTiempoPreparacionMin(request.getTiempoPreparacionMin());
        restaurante.setTiempoPreparacionMax(request.getTiempoPreparacionMax());
        restaurante.setCapacidadCocina(request.getCapacidadCocina());

        restauranteRepository.save(restaurante);

        return restauranteMapper.toResponse(restaurante);
    }

    // ACTUALIZAR UBICACIÓN
    @Override
    @Transactional
    public void actualizarUbicacion(UbicacionRestauranteRequestDTO request) {

        Usuario usuario = getUsuarioAutenticado();

        Restaurante restaurante = restauranteRepository
                .findById(usuario.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        restaurante.setLatitud(request.getLatitud());
        restaurante.setLongitud(request.getLongitud());

        restauranteRepository.save(restaurante);
    }

    // CAMBIAR ESTADO
    @Override
    @Transactional
    public void cambiarEstado(String estado) {

        Usuario usuario = getUsuarioAutenticado();

        Restaurante restaurante = restauranteRepository
                .findById(usuario.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        restaurante.setEstado(EstadoRestaurante.valueOf(estado));

        restauranteRepository.save(restaurante);
    }

    // CREAR DESDE REGISTER
    @Override
    @Transactional
    public void crearDesdeRegister(
            Usuario usuario,
            String nombreComercial,
            String razonSocial,
            String ruc,
            String direccionFiscal
    ) {

        Restaurante restaurante = Restaurante.builder()
                .usuario(usuario)
                .nombreComercial(nombreComercial)
                .razonSocial(razonSocial)
                .ruc(ruc)
                .direccionFiscal(direccionFiscal)
                .tiempoPreparacionMin(10)
                .tiempoPreparacionMax(20)
                .capacidadCocina(5)
                .estado(EstadoRestaurante.ABIERTO)
                .build();

        restauranteRepository.save(restaurante);
    }
}