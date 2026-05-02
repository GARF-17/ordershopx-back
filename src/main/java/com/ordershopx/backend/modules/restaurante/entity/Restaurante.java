package com.ordershopx.backend.modules.restaurante.entity;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.shared.enums.EstadoRestaurante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "restaurantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    @Column(name = "id_usuario")
    private UUID idUsuario;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "nombre_comercial", nullable = false, length = 150)
    private String nombreComercial;

    @Column(name = "razon_social", length = 150)
    private String razonSocial;

    @Column(name = "ruc", length = 20, unique = true)
    private String ruc;

    @Column(name = "direccion_fiscal", length = 255)
    private String direccionFiscal;

    @Column(name = "tiempo_preparacion_min")
    private Integer tiempoPreparacionMin;

    @Column(name = "tiempo_preparacion_max")
    private Integer tiempoPreparacionMax;

    @Column(name = "capacidad_cocina")
    private Integer capacidadCocina;

    @Column(name = "calificacion_promedio", precision = 3, scale = 2)
    private BigDecimal calificacionPromedio;

    @Column(name = "total_resenas")
    private Integer totalResenas;

    @Column(name = "imagen_portada_url")
    private String imagenPortadaUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", columnDefinition = "estado_restaurante")
    private EstadoRestaurante estado;

    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;
}