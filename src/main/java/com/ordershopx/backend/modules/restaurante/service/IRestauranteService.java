package com.ordershopx.backend.modules.restaurante.service;

import com.ordershopx.backend.modules.restaurante.dto.request.RestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.request.UbicacionRestauranteRequestDTO;
import com.ordershopx.backend.modules.restaurante.dto.response.RestauranteResponseDTO;
import com.ordershopx.backend.modules.usuario.entity.Usuario;

public interface IRestauranteService {

    RestauranteResponseDTO obtenerMiRestaurante();

    RestauranteResponseDTO actualizarRestaurante(RestauranteRequestDTO request);

    void actualizarUbicacion(UbicacionRestauranteRequestDTO request);

    void cambiarEstado(String estado);

    void crearDesdeRegister(Usuario usuario, String nombreComercial,
                            String razonSocial, String ruc, String direccionFiscal);


}