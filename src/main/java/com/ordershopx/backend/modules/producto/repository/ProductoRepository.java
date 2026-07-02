package com.ordershopx.backend.modules.producto.repository;

import com.ordershopx.backend.modules.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    List<Producto> findByCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNullOrderByNombreAsc(UUID idRestaurante);
    List<Producto> findByCategoria_IdCategoriaAndEliminadoEnIsNullOrderByNombreAsc(UUID idCategoria);
    List<Producto> findByCategoria_Restaurante_IdUsuarioAndEstaDisponibleTrueAndEliminadoEnIsNullOrderByNombreAsc(UUID idRestaurante);
    List<Producto> findByCategoria_Restaurante_IdUsuarioAndStockGreaterThanAndEliminadoEnIsNullOrderByNombreAsc(UUID idRestaurante,Integer stock);
    Optional<Producto> findByIdProductoAndCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(UUID idProducto, UUID idRestaurante);
    boolean existsByCategoria_IdCategoriaAndNombreIgnoreCaseAndEliminadoEnIsNull(UUID idCategoria, String nombre);
    long countByCategoria_Restaurante_IdUsuarioAndEliminadoEnIsNull(UUID idRestaurante);
}