package com.ordershopx.backend.modules.staff.entity;

import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.shared.entity.BaseCreacionEntity;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(
        name = "usuarios_restaurante",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id_usuario", "id_restaurante"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRestaurante extends BaseCreacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_usuario_restaurante", updatable = false, nullable = false)
    private UUID idUsuarioRestaurante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante", nullable = false)
    private Restaurante restaurante;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rol", columnDefinition = "rol_restaurante", nullable = false)
    private RolRestaurante rol;

    @Column(name = "es_principal")
    private Boolean esPrincipal;

    @Column(name = "esta_activo")
    private Boolean estaActivo;

}