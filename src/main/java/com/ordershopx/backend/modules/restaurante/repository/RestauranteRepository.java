package com.ordershopx.backend.modules.restaurante.repository;

import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestauranteRepository extends JpaRepository<Restaurante, UUID> {

    boolean existsByRuc(String ruc);
    Optional<Restaurante> findByRuc(String ruc);
    Optional<Restaurante> findByUsuario(Usuario usuario);

    @Query(value = """
        SELECT * FROM restaurantes r
        WHERE r.latitud IS NOT NULL 
          AND r.longitud IS NOT NULL
          AND (
            6371 * acos(
                cos(radians(:latUsuario)) * cos(radians(r.latitud)) * cos(radians(r.longitud) - radians(:lngUsuario)) + 
                sin(radians(:latUsuario)) * sin(radians(r.latitud))
            )
        ) <= :radioKm
        """, nativeQuery = true)
    List<Restaurante> findRestaurantesCercanos(
            @Param("latUsuario") Double latUsuario,
            @Param("lngUsuario") Double lngUsuario,
            @Param("radioKm") Double radioKm
    );
}