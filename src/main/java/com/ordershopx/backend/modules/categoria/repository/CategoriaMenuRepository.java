package com.ordershopx.backend.modules.categoria.repository;

import com.ordershopx.backend.modules.categoria.entity.CategoriaMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaMenuRepository extends JpaRepository<CategoriaMenu, UUID> {

    List<CategoriaMenu> findByRestaurante_IdUsuarioOrderByOrdenVisualAsc(UUID idRestaurante);
    List<CategoriaMenu> findByRestaurante_IdUsuarioAndEliminadoEnIsNullOrderByOrdenVisualAsc(UUID idRestaurante);
    boolean existsByRestaurante_IdUsuarioAndNombreIgnoreCaseAndEliminadoEnIsNull(UUID idRestaurante, String nombre);
    Optional<CategoriaMenu> findByIdCategoriaAndRestaurante_IdUsuarioAndEliminadoEnIsNull(UUID idCategoria, UUID idRestaurante);
    long countByRestaurante_IdUsuarioAndEliminadoEnIsNull(UUID idRestaurante);
    @Query("SELECT c FROM CategoriaMenu c WHERE c.restaurante.idUsuario = :idRestaurante AND c.eliminadoEn IS NULL ORDER BY c.ordenVisual ASC")
    List<CategoriaMenu> listarActivasPorRestaurante(@Param("idRestaurante") UUID idRestaurante);
}