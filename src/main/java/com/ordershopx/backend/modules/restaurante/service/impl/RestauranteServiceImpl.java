package com.ordershopx.backend.modules.restaurante.service.impl;

import com.ordershopx.backend.modules.pedido.repository.PedidoRepository;
import com.ordershopx.backend.modules.restaurante.dto.request.*;
import com.ordershopx.backend.modules.restaurante.dto.response.*;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.restaurante.mapper.RestauranteMapper;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.restaurante.service.IRestauranteService;
import com.ordershopx.backend.modules.staff.entity.UsuarioRestaurante;
import com.ordershopx.backend.modules.staff.repository.UsuarioRestauranteRepository;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.modules.usuario.service.IUsuarioService;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.enums.EstadoRestaurante;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import com.ordershopx.backend.shared.exception.ResourceNotFoundException;
import com.ordershopx.backend.shared.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    private final UsuarioRestauranteRepository usuarioRestauranteRepository;

    private Usuario getUsuarioAutenticado() {
        return usuarioService.obtenerPorCorreo(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private Restaurante getAsignacionYValidarPermisos(Usuario usuario, boolean requiereAdmin) {
        UsuarioRestaurante asignacion = usuarioRestauranteRepository.findFirstByUsuarioUsuarioIdAndEstaActivoTrue(usuario.getUsuarioId())
                .orElseThrow(() -> new UnauthorizedException("No estás asignado a ningún restaurante activo."));

        if (requiereAdmin) {
            RolRestaurante rol = asignacion.getRol();
            if (rol != RolRestaurante.OWNER && rol != RolRestaurante.ADMIN_LOCAL) {
                throw new UnauthorizedException("Permisos insuficientes. Solo OWNER o ADMIN_LOCAL pueden modificar datos del local.");
            }
        }
        return asignacion.getRestaurante();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioDisponibleDTO> listarHorariosDisponibles(UUID idRestaurante) {
        // Lógica intacta, es pública para el comensal
        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        int tiempoMin = restaurante.getTiempoPreparacionMin() != null ? restaurante.getTiempoPreparacionMin() : 10;
        int capacidad = restaurante.getCapacidadCocina() != null ? restaurante.getCapacidadCocina() : 5;

        List<HorarioDisponibleDTO> horarios = new ArrayList<>();
        ZoneOffset zoneOffset = ZoneOffset.ofHours(-5);
        OffsetDateTime ahoraPeru = OffsetDateTime.now(zoneOffset).withSecond(0).withNano(0);
        OffsetDateTime primerBloqueDisponible = ahoraPeru.plusMinutes(tiempoMin);

        int minutosSobrantes = primerBloqueDisponible.getMinute() % 10;
        if (minutosSobrantes > 0) {
            primerBloqueDisponible = primerBloqueDisponible.plusMinutes(10 - minutosSobrantes);
        }

        for (int i = 0; i < 12; i++) {
            OffsetDateTime inicioSlot = primerBloqueDisponible.plusMinutes(i * 10L);
            OffsetDateTime finSlot = inicioSlot.plusMinutes(10);

            long pedidosEnCocina = pedidoRepository.countByHorarioRango(idRestaurante, List.of(EstadoPedido.EN_COLA, EstadoPedido.PREPARANDO), inicioSlot, finSlot);
            boolean disponible = pedidosEnCocina < capacidad;
            int cuposRestantes = Math.max(0, capacidad - (int) pedidosEnCocina);

            horarios.add(HorarioDisponibleDTO.builder().hora(inicioSlot).cuposDisponibles(cuposRestantes).disponible(disponible).build());
        }
        return horarios;
    }

    @Override
    public RestauranteResponseDTO obtenerMiRestaurante() {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, false);
        return restauranteMapper.toResponse(restaurante);
    }

    @Override
    public List<RestauranteResponseDTO> listarRestaurantes() {
        return restauranteRepository.findAll().stream().map(restauranteMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesCercanos(Double latitud, Double longitud, Double radioKm) {
        return restauranteRepository.findRestaurantesCercanos(latitud, longitud, radioKm)
                .stream().map(restauranteMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public RestauranteResponseDTO actualizarRestaurante(RestauranteRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true);

        restaurante.setNombreComercial(request.getNombreComercial());
        restaurante.setRazonSocial(request.getRazonSocial());
        restaurante.setDireccionFiscal(request.getDireccionFiscal());
        restaurante.setTiempoPreparacionMin(request.getTiempoPreparacionMin());
        restaurante.setTiempoPreparacionMax(request.getTiempoPreparacionMax());
        restaurante.setCapacidadCocina(request.getCapacidadCocina());

        return restauranteMapper.toResponse(restauranteRepository.save(restaurante));
    }

    @Override
    @Transactional
    public void actualizarUbicacion(UbicacionRestauranteRequestDTO request) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true);

        restaurante.setLatitud(request.getLatitud());
        restaurante.setLongitud(request.getLongitud());
        restauranteRepository.save(restaurante);
    }

    @Override
    @Transactional
    public void cambiarEstado(String estado) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true); // 🔥 REGLA

        restaurante.setEstado(EstadoRestaurante.valueOf(estado));
        restauranteRepository.save(restaurante);
    }

    @Override
    @Transactional
    public void crearDesdeRegister(Usuario usuario, String nombreComercial, String razonSocial, String ruc, String direccionFiscal) {
        Restaurante restaurante = Restaurante.builder()
                .usuario(usuario).nombreComercial(nombreComercial).razonSocial(razonSocial).ruc(ruc)
                .direccionFiscal(direccionFiscal).tiempoPreparacionMin(10).tiempoPreparacionMax(20)
                .capacidadCocina(5).estado(EstadoRestaurante.ABIERTO).build();
        restauranteRepository.save(restaurante);
    }
}