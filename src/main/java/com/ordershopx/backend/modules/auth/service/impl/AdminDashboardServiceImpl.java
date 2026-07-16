package com.ordershopx.backend.modules.auth.service.impl;
import com.ordershopx.backend.modules.auth.dto.response.DashboardResumenDTO;
import com.ordershopx.backend.modules.auth.service.IAdminDashboardService;
import com.ordershopx.backend.modules.onboarding.repository.SolicitudRestauranteRepository;
import com.ordershopx.backend.modules.restaurante.repository.RestauranteRepository;
import com.ordershopx.backend.modules.usuario.repository.UsuarioRepository;
import com.ordershopx.backend.shared.enums.EstadoRestaurante;
import com.ordershopx.backend.shared.enums.EstadoSolicitudRestaurante;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardServiceImpl implements IAdminDashboardService{
    private final UsuarioRepository usuarioRepository;
    private final RestauranteRepository restauranteRepository;
    private final SolicitudRestauranteRepository solicitudRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResumenDTO obtenerResumen() {
        log.info("event=service_obtener_resumen_dashboard");

        long solAprobadas = solicitudRepository.countByEstado(EstadoSolicitudRestaurante.APROBADA);
        long solRechazadas = solicitudRepository.countByEstado(EstadoSolicitudRestaurante.RECHAZADA);
        long totalProcesadas = solAprobadas + solRechazadas;

        int tasa = totalProcesadas > 0 ? (int) ((solAprobadas * 100) / totalProcesadas) : 0;

        return DashboardResumenDTO.builder()
                .alertasRevision(solicitudRepository.countByEstado(EstadoSolicitudRestaurante.PENDIENTE))
                .alertasActivacion(solicitudRepository.countByEstado(EstadoSolicitudRestaurante.ACTIVACION_PENDIENTE))
                .usuariosTotal(usuarioRepository.count())
                .usuariosActivos(usuarioRepository.countByEstaActivoTrue())
                .restaurantesTotal(restauranteRepository.count())
                .restaurantesActivos(restauranteRepository.countByEstadoNot(EstadoRestaurante.SUSPENDIDO))
                .restaurantesSuspendidos(restauranteRepository.countByEstado(EstadoRestaurante.SUSPENDIDO))
                .solPendientes(solicitudRepository.countByEstado(EstadoSolicitudRestaurante.PENDIENTE))
                .solEnRevision(solicitudRepository.countByEstado(EstadoSolicitudRestaurante.EN_REVISION))
                .solAprobadas(solAprobadas)
                .solRechazadas(solRechazadas)
                .tasaAprobacion(tasa)
                .build();
    }
}

