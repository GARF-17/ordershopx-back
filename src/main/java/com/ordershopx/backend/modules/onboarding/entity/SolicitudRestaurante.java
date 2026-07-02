package com.ordershopx.backend.modules.onboarding.entity;

import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.shared.entity.BaseEntity;
import com.ordershopx.backend.shared.enums.EstadoSolicitudRestaurante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRestaurante extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_solicitud", updatable = false, nullable = false)
    private UUID idSolicitud;

    @Column(name = "ruc", length = 20, unique = true, nullable = false)
    private String ruc;

    @Column(name = "nombre_comercial", length = 150, nullable = false)
    private String nombreComercial;

    @Column(name = "razon_social", length = 200)
    private String razonSocial;

    @Column(name = "direccion_fiscal", columnDefinition = "TEXT")
    private String direccionFiscal;

    @Column(name = "correo_contacto", length = 150)
    private String correoContacto;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    @Column(name = "latitud", precision = 10, scale = 8)
    private BigDecimal latitud;

    @Column(name = "longitud", precision = 11, scale = 8)
    private BigDecimal longitud;

    @Column(name = "encargado_nombre", length = 150)
    private String encargadoNombre;

    @Column(name = "encargado_apellido", length = 150)
    private String encargadoApellido;

    @Column(name = "encargado_dni", length = 20)
    private String encargadoDni;

    @Column(name = "encargado_telefono", length = 20)
    private String encargadoTelefono;

    @Column(name = "encargado_correo", length = 150)
    private String encargadoCorreo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", columnDefinition = "estado_solicitud_restaurante")
    private EstadoSolicitudRestaurante estado;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "cantidad_reenvios")
    private Integer cantidadReenvios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobado_por")
    private Usuario aprobadoPor;

    @Column(name = "fecha_revision")
    private OffsetDateTime fechaRevision;

}