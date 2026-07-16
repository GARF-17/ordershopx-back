package com.ordershopx.backend.modules.restaurante.service.impl;

import com.ordershopx.backend.modules.pedido.entity.Pedido;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    // ==============================================================
    // MÉTODO PARA LISTAR USUARIOS INTERNOS (STAFF)
    // ==============================================================
    @Override
    @Transactional(readOnly = true)
    public List<com.ordershopx.backend.modules.staff.dto.response.StaffResponseDTO> listarStaffRestaurante() {
        Usuario usuarioActual = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuarioActual, false);

        // Buscamos todos los trabajadores de este restaurante en específico
        List<UsuarioRestaurante> staff = usuarioRestauranteRepository.findByRestauranteIdUsuario(restaurante.getIdUsuario());

        return staff.stream().map(ur -> {
            Usuario u = ur.getUsuario();

            // Como la entidad Usuario no tiene un campo 'nombre',
            // extraemos la primera parte de su correo electrónico para mostrar algo amigable
            String nombreCompleto = u.getCorreoElectronico().split("@")[0];

            String inicial = nombreCompleto.substring(0, 1).toUpperCase();

            return com.ordershopx.backend.modules.staff.dto.response.StaffResponseDTO.builder()
                    .idUsuarioRestaurante(ur.getIdUsuarioRestaurante())
                    .idUsuario(u.getUsuarioId())
                    .nombre(nombreCompleto)
                    .inicial(inicial)
                    .rol(ur.getRol().name())
                    .estaActivo(ur.getEstaActivo() != null ? ur.getEstaActivo() : false)
                    .correo(u.getCorreoElectronico())
                    .telefono(u.getTelefono() != null ? u.getTelefono() : "No registrado")
                    .fechaVinculacion(ur.getFechaCreacion())
                    .ultimoAcceso(u.getFechaUltimoLogin())
                    .build();
        }).toList();
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
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, true);

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

    @Override
    @Transactional
    public void suspenderRestauranteAdmin(UUID idRestaurante, boolean suspender) {
        log.info("event=admin_suspender_restaurante id={} suspender={}", idRestaurante, suspender);

        Restaurante restaurante = restauranteRepository.findById(idRestaurante)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado"));

        if (suspender) {
            restaurante.setEstado(EstadoRestaurante.SUSPENDIDO);
        } else {
            restaurante.setEstado(EstadoRestaurante.CERRADO);
        }

        restauranteRepository.save(restaurante);
    }

    // ==============================================================
    // MÉTODO PARA EL DASHBOARD DEL RESTAURANTE
    // ==============================================================
    @Override
    @Transactional(readOnly = true)
    public RestauranteDashboardDTO obtenerResumenDashboard() {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, false);
        UUID idRes = restaurante.getIdUsuario();

        ZoneOffset offset = ZoneOffset.ofHours(-5);
        OffsetDateTime ahora = OffsetDateTime.now(offset);
        OffsetDateTime inicioHoy = ahora.withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime finHoy = ahora.withHour(23).withMinute(59).withSecond(59).withNano(999999999);


        OffsetDateTime inicioSemanaPasada = inicioHoy.minusDays(7);
        OffsetDateTime finSemanaPasada = finHoy.minusDays(7);

        // 1. Ingresos
        BigDecimal ingresosHoy = pedidoRepository.sumIngresosByRestauranteAndEstadoAndFecha(idRes, EstadoPedido.COMPLETADO, inicioHoy, finHoy);
        if (ingresosHoy == null) ingresosHoy = BigDecimal.ZERO;

        BigDecimal ingresosSemanaPasada = pedidoRepository.sumIngresosByRestauranteAndEstadoAndFecha(idRes, EstadoPedido.COMPLETADO, inicioSemanaPasada, finSemanaPasada);
        if (ingresosSemanaPasada == null) ingresosSemanaPasada = BigDecimal.ZERO;

        // Crecimiento
        int crecimiento = 0;
        if (ingresosSemanaPasada.compareTo(BigDecimal.ZERO) > 0) {
            crecimiento = ingresosHoy.subtract(ingresosSemanaPasada)
                    .divide(ingresosSemanaPasada, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100)).intValue();
        } else if (ingresosHoy.compareTo(BigDecimal.ZERO) > 0) {
            crecimiento = 100;
        }


        long pedidosHoy = pedidoRepository.countPedidosEnRango(idRes, inicioHoy, finHoy);
        long completadosHoy = pedidoRepository.countPedidosByEstadoEnRango(idRes, EstadoPedido.COMPLETADO, inicioHoy, finHoy);
        long canceladosHoy = pedidoRepository.countPedidosByEstadoEnRango(idRes, EstadoPedido.CANCELADO, inicioHoy, finHoy);

        List<EstadoPedido> estadosActivos = List.of(EstadoPedido.PENDIENTE, EstadoPedido.EN_COLA, EstadoPedido.PREPARANDO, EstadoPedido.LISTO_PARA_RECOGER, EstadoPedido.ATRASADO);
        long activosHoy = pedidoRepository.countPedidosActivos(idRes, estadosActivos); // Reutilizamos tu método de capacidad global


        long clientesUnicos = pedidoRepository.countClientesUnicosEnRango(idRes, inicioHoy, finHoy);
        Double tiempoPromedioBD = pedidoRepository.avgTiempoPreparacionEnRango(idRes, EstadoPedido.COMPLETADO, inicioHoy, finHoy);


        int tiempoPromedio = tiempoPromedioBD != null ? tiempoPromedioBD.intValue() : 18; // Mock 18min si no hay pedidos

        BigDecimal ticketPromedio = BigDecimal.ZERO;
        if (completadosHoy > 0) {
            ticketPromedio = ingresosHoy.divide(new BigDecimal(completadosHoy), 2, RoundingMode.HALF_UP);
        }

        return RestauranteDashboardDTO.builder()
                .ingresosTotales(ingresosHoy)
                .porcentajeCrecimiento(crecimiento)
                .pedidosHoy(pedidosHoy)
                .ticketPromedio(ticketPromedio)
                .pedidosCompletados(completadosHoy)
                .pedidosActivos(activosHoy)
                .pedidosCancelados(canceladosHoy)
                .calificacion(restaurante.getCalificacionPromedio() != null ? restaurante.getCalificacionPromedio() : BigDecimal.ZERO)
                .totalResenas(restaurante.getTotalResenas() != null ? restaurante.getTotalResenas() : 0)
                .tiempoPromedioPrep(tiempoPromedio)
                .clientesUnicos(clientesUnicos)
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public RestauranteReporteDTO obtenerReportesRestaurante(String periodo) {
        Usuario usuario = getUsuarioAutenticado();
        Restaurante restaurante = getAsignacionYValidarPermisos(usuario, false);
        UUID idRes = restaurante.getIdUsuario();

        ZoneOffset offset = ZoneOffset.ofHours(-5);
        OffsetDateTime ahora = OffsetDateTime.now(offset);
        OffsetDateTime inicioActual, finActual, inicioAnterior, finAnterior;

        if ("mes".equalsIgnoreCase(periodo)) {
            inicioActual = ahora.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            finActual = ahora;
            inicioAnterior = inicioActual.minusMonths(1);
            finAnterior = inicioActual.minusSeconds(1);
        } else {
            // Default "Esta semana" (Últimos 7 días)
            inicioActual = ahora.minusDays(7).withHour(0).withMinute(0).withSecond(0);
            finActual = ahora;
            inicioAnterior = inicioActual.minusDays(7);
            finAnterior = inicioActual.minusSeconds(1);
        }

        List<Pedido> actuales = pedidoRepository.findPedidosParaReporte(idRes, inicioActual, finActual);
        List<Pedido> anteriores = pedidoRepository.findPedidosParaReporte(idRes, inicioAnterior, finAnterior);


        long pedActuales = actuales.size();
        long cancActuales = actuales.stream().filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count();
        BigDecimal ingActuales = actuales.stream().filter(p -> p.getEstado() == EstadoPedido.COMPLETADO)
                .map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tkActual = pedActuales > 0 ? ingActuales.divide(new BigDecimal(pedActuales), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;


        long pedAnteriores = anteriores.size();
        long cancAnteriores = anteriores.stream().filter(p -> p.getEstado() == EstadoPedido.CANCELADO).count();
        BigDecimal ingAnteriores = anteriores.stream().filter(p -> p.getEstado() == EstadoPedido.COMPLETADO)
                .map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tkAnterior = pedAnteriores > 0 ? ingAnteriores.divide(new BigDecimal(pedAnteriores), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;


        int ingCambio = ingAnteriores.compareTo(BigDecimal.ZERO) > 0 ? ingActuales.subtract(ingAnteriores).divide(ingAnteriores, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue() : (ingActuales.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0);
        int pedCambio = pedAnteriores > 0 ? (int) (((pedActuales - pedAnteriores) * 100) / pedAnteriores) : (pedActuales > 0 ? 100 : 0);
        int tkCambio = tkAnterior.compareTo(BigDecimal.ZERO) > 0 ? tkActual.subtract(tkAnterior).divide(tkAnterior, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue() : 0;
        int cancCambio = (int) (cancActuales - cancAnteriores); // En cancelados mostramos la diferencia directa (-1, +2)


        int[] conteoHoras = new int[24];
        for (Pedido p : actuales) {
            conteoHoras[p.getFechaCreacion().atZoneSameInstant(offset).getHour()]++;
        }
        List<RestauranteReporteDTO.HoraPicoDTO> horasPico = new ArrayList<>();

        int[] horasFijas = {12, 13, 14, 19, 20, 21};
        for (int h : horasFijas) {
            horasPico.add(new RestauranteReporteDTO.HoraPicoDTO(h + "-" + (h + 1) + "h", conteoHoras[h]));
        }


        java.util.Map<String, RestauranteReporteDTO.TopProductoDTO> prodMap = new java.util.HashMap<>();
        for (Pedido p : actuales) {
            if (p.getEstado() != EstadoPedido.CANCELADO) {
                for (com.ordershopx.backend.modules.pedido.entity.DetallePedido dp : p.getDetalles()) {
                    String nombre = dp.getNombreHistorico();
                    RestauranteReporteDTO.TopProductoDTO dto = prodMap.getOrDefault(nombre, new RestauranteReporteDTO.TopProductoDTO(nombre, 0, BigDecimal.ZERO, 0));
                    dto.setCantidad(dto.getCantidad() + dp.getCantidad());
                    dto.setMonto(dto.getMonto().add(dp.getSubtotal()));
                    prodMap.put(nombre, dto);
                }
            }
        }

        List<RestauranteReporteDTO.TopProductoDTO> topProductos = new ArrayList<>(prodMap.values());
        topProductos.sort((a, b) -> Integer.compare(b.getCantidad(), a.getCantidad())); // Ordenar mayor a menor
        if (topProductos.size() > 4) topProductos = topProductos.subList(0, 4);

        int maxCantidad = topProductos.stream().mapToInt(RestauranteReporteDTO.TopProductoDTO::getCantidad).max().orElse(1);
        for (RestauranteReporteDTO.TopProductoDTO dto : topProductos) {
            dto.setPorcentaje((dto.getCantidad() * 100) / maxCantidad);
        }

        return RestauranteReporteDTO.builder()
                .ingresos(ingActuales).ingresosCambio(ingCambio)
                .pedidos(pedActuales).pedidosCambio(pedCambio)
                .ticketProm(tkActual).ticketCambio(tkCambio)
                .cancelados(cancActuales).canceladosCambio(cancCambio)
                .horasPico(horasPico)
                .topProductos(topProductos)
                .build();
    }
}