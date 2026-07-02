package com.ordershopx.backend.modules.staff.service;

import com.ordershopx.backend.modules.staff.dto.request.ActualizarStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.CrearInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.request.ValidarInvitacionStaffRequestDTO;
import com.ordershopx.backend.modules.staff.dto.response.InvitacionStaffResponseDTO;
import com.ordershopx.backend.modules.staff.dto.response.UsuarioRestauranteResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IStaffService {

    List<UsuarioRestauranteResponseDTO> listarStaffActivo(UUID idRestaurante);
    List<InvitacionStaffResponseDTO> listarInvitacionesPendientes(UUID idRestaurante);
    void invitarEmpleado(CrearInvitacionStaffRequestDTO dto, UUID idOwner);
    void validarInvitacion(ValidarInvitacionStaffRequestDTO dto);
    void actualizarEmpleado(UUID idRestaurante, UUID idUsuarioTarget, ActualizarStaffRequestDTO dto, UUID idOwner);
}