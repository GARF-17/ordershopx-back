package com.ordershopx.backend.modules.cliente.service;

import com.ordershopx.backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.request.UbicacionRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.response.ClienteResponseDTO;

public interface IClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO request);

    ClienteResponseDTO obtenerMiCliente();

    void actualizarUbicacion(UbicacionRequestDTO request);
}