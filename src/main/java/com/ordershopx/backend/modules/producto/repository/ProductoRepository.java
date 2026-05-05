package com.ordershopx.backend.modules.producto.repository;

import com.ordershopx.backend.modules.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    // LISTAR PRODUCTOS
    List<Producto> findByCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNullOrderByNombreAsc(
            UUID idRestaurante
    );

    // LISTAR PRODUCTOS POR CATEGORÍA
    List<Producto> findByCategoria_IdCategoriaAndEliminadoEnIsNullOrderByNombreAsc(
            UUID idCategoria
    );

    // LISTAR PRODUCTOS DISPONIBLES
    List<Producto> findByCategoria_Restaurante_IdUsuarioAndEstaDisponibleTrueAndEliminadoEnIsNullOrderByNombreAsc(
            UUID idRestaurante
    );

    // LISTAR PRODUCTOS CON STOCK
    List<Producto> findByCategoria_Restaurante_IdUsuarioAndStockGreaterThanAndEliminadoEnIsNullOrderByNombreAsc(
            UUID idRestaurante,
            Integer stock
    );

    // OBTENER PRODUCTO (SEGURIDAD)
    Optional<Producto> findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(
            UUID idProducto,
            UUID idRestaurante
    );

    // VALIDAR DUPLICADO EN CATEGORÍA
    boolean existsByCategoria_IdCategoriaAndNombreIgnoreCaseAndEliminadoEnIsNull(
            UUID idCategoria,
            String nombre
    );

    // CONTAR PRODUCTOS ACTIVOS DEL RESTAURANTE
    long countByCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(
            UUID idRestaurante
    );

}