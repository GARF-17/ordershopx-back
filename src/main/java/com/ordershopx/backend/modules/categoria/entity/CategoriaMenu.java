package com.ordershopx.backend.modules.categoria.entity;

import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "categorias_menu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaMenu {

    @Id
    @GeneratedValue
    @Column(name = "id_categoria")
    private UUID idCategoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_restaurante", nullable = false)
    private Restaurante restaurante;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "orden_visual")
    private Integer ordenVisual;

    @Column(name = "eliminado_en")
    private OffsetDateTime eliminadoEn;
}