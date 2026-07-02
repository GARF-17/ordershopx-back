package com.ordershopx.backend.modules.valoracion.service;

import com.ordershopx.backend.modules.valoracion.dto.request.ValoracionRequestDTO;
import com.ordershopx.backend.modules.valoracion.dto.response.ValoracionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IValoracionService {

    ValoracionResponseDTO registrarValoracion(ValoracionRequestDTO request);
    ValoracionResponseDTO obtenerValoracionPorId(UUID idValoracion);
    ValoracionResponseDTO obtenerValoracionPorPedido(UUID idPedido);
    List<ValoracionResponseDTO> listarValoracionesRestaurante(UUID idRestaurante);
    List<ValoracionResponseDTO> listarMisValoraciones();
    void eliminarValoracion(UUID idValoracion);
}