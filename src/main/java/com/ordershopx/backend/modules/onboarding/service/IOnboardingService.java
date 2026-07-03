package com.ordershopx.backend.modules.onboarding.service;

import com.ordershopx.backend.modules.onboarding.dto.request.SolicitudRestauranteRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.request.ValidarInvitacionRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.AprobacionResponseDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.SolicitudRestauranteResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface IOnboardingService {

    SolicitudRestauranteResponseDTO registrarSolicitud(SolicitudRestauranteRequestDTO request);

    @Transactional
    AprobacionResponseDTO aprobarSolicitud(UUID idSolicitud, String adminCorreo);

    void validarInvitacion(ValidarInvitacionRequestDTO request);

    SolicitudRestauranteResponseDTO consultarEstado(UUID solicitudId);

    void aprobarSolicitudYGenerarPin(UUID idSolicitud);

    // 🔥 NUEVO MÉTODO AGREGADO: Para listar todas las solicitudes
    List<SolicitudRestauranteResponseDTO> listarSolicitudes();
}