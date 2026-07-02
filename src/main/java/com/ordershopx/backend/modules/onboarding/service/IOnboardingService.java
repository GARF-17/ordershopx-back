package com.ordershopx.backend.modules.onboarding.service;

import com.ordershopx.backend.modules.onboarding.dto.request.SolicitudRestauranteRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.request.ValidarInvitacionRequestDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.AprobacionResponseDTO;
import com.ordershopx.backend.modules.onboarding.dto.response.SolicitudRestauranteResponseDTO;

import java.util.UUID;

public interface IOnboardingService {

    SolicitudRestauranteResponseDTO registrarSolicitud(SolicitudRestauranteRequestDTO dto);
    AprobacionResponseDTO aprobarSolicitud(UUID idSolicitud, String adminCorreo);
    void validarInvitacion(ValidarInvitacionRequestDTO dto);
    SolicitudRestauranteResponseDTO consultarEstado(UUID solicitudId);
}