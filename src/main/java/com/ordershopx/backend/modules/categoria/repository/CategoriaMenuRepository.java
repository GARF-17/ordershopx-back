package com.ordershopx.backend.modules.categoria.repository;

import com.ordershopx.backend.modules.categoria.entity.CategoriaMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaMenuRepository extends JpaRepository<CategoriaMenu, UUID> {

    // Obtener categorías del restaurante
    List<CategoriaMenu> findByRestaurante_IdUsuarioOrderByOrdenVisualAsc(UUID idRestaurante);

    // Obtener solo ACTIVAS
    List<CategoriaMenu> findByRestaurante_IdUsuarioAndEliminadoEnIsNullOrderByOrdenVisualAsc(UUID idRestaurante);

    // Validar duplicados
    boolean existsByRestaurante_IdUsuarioAndNombreIgnoreCaseAndEliminadoEnIsNull(
            UUID idRestaurante,
            String nombre
    );

    // Buscar categoría del restaurante
    Optional<CategoriaMenu> findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(
            UUID idCategoria,
            UUID idRestaurante
    );

    // Contar categorías activas
    long countByRestaurante_IdUsuarioAndEliminadoEnIsNull(UUID idRestaurante);
}