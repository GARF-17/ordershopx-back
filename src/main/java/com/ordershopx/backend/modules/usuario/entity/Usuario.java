package com.ordershopx.backend.modules.usuario.entity;

import com.ordershopx.backend.shared.entity.BaseEntity;
import com.ordershopx.backend.shared.enums.RolGlobal;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario")
    private UUID usuarioId;

    @Column(name = "correo_electronico", nullable = false, unique = true, length = 150)
    private String correoElectronico;

    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento = "DNI";

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String claveHash;

    @Column(name = "telefono", nullable = false, unique = true, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rol", nullable = false, columnDefinition = "rol_global")
    private RolGlobal rol;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "ultimo_login")
    private OffsetDateTime fechaUltimoLogin;
}