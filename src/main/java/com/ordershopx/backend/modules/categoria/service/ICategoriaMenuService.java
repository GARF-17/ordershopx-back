package com.ordershopx.backend.modules.categoria.service;

import com.ordershopx.backend.modules.categoria.dto.request.CategoriaMenuRequestDTO;
import com.ordershopx.backend.modules.categoria.dto.response.CategoriaMenuResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ICategoriaMenuService {


    CategoriaMenuResponseDTO crearCategoria(CategoriaMenuRequestDTO request);
    List<CategoriaMenuResponseDTO> listarMisCategorias();
    CategoriaMenuResponseDTO actualizarCategoria(UUID idCategoria, CategoriaMenuRequestDTO request);
    void eliminarCategoria(UUID idCategoria);
    List<CategoriaMenuResponseDTO> listarCategoriasPorRestaurante(UUID idRestaurante);
}