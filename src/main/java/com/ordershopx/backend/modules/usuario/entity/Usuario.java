package com.ordershopx.backend.modules.usuario.entity;

import com.ordershopx.backend.shared.entity.BaseEntity;
import com.ordershopx.backend.shared.enums.TipoRol;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String claveHash;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private TipoRol rol;

    @Column(name = "esta_activo")
    private Boolean estaActivo = true;

}
