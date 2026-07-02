package com.ordershopx.backend.modules.cliente.service;

import com.ordershopx.backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.request.PreferenciasRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.request.UbicacionRequestDTO;
import com.ordershopx.backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.ordershopx.backend.modules.usuario.entity.Usuario;

public interface IClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO request);
    ClienteResponseDTO obtenerMiCliente();
    void actualizarUbicacion(UbicacionRequestDTO request);
    void crearDesdeRegister(Usuario usuario, String nombre, String apellido);
    void actualizarPreferencias(PreferenciasRequestDTO request);

}