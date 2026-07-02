package com.ordershopx.backend.modules.producto.service;

import com.ordershopx.backend.modules.producto.dto.request.ProductoRequestDTO;
import com.ordershopx.backend.modules.producto.dto.response.ProductoResponseDTO;

import java.util.List;
import java.util.UUID;

public interface IProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO request);
    List<ProductoResponseDTO> listarMisProductos();
    List<ProductoResponseDTO> listarPorCategoria(UUID idCategoria);
    List<ProductoResponseDTO> listarProductosCliente(UUID idRestaurante);
    ProductoResponseDTO actualizarProducto(UUID idProducto, ProductoRequestDTO request);
    void eliminarProducto(UUID idProducto);
    void cambiarDisponibilidad(UUID idProducto, Boolean disponible);
}