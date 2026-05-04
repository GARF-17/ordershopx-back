package com.ordershopx.backend.modules.categoria.service;

import com.ordershopx.backend.modules.categoria.dto.request.CategoriaMenuRequestDTO;
import com.ordershopx.backend.modules.categoria.dto.response.CategoriaMenuResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ICategoriaMenuService {


    // Crear Categoria
    CategoriaMenuResponseDTO crearCategoria(CategoriaMenuRequestDTO request);

    // Listar Categorias
    List<CategoriaMenuResponseDTO> listarMisCategorias();

    // Actualizar Categoria
    CategoriaMenuResponseDTO actualizarCategoria(UUID idCategoria,
                                                 CategoriaMenuRequestDTO request);

    // Eliminar Categoria
    void eliminarCategoria(UUID idCategoria);
}