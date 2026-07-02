package com.ordershopx.backend.modules.staff.entity;

import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.shared.entity.BaseCreacionEntity;
import com.ordershopx.backend.shared.enums.RolRestaurante;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitaciones_staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitacionStaff extends BaseCreacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_invitacion", updatable = false, nullable = false)
    private UUID idInvitacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante")
    private Restaurante restaurante;

    @Column(name = "correo", length = 150)
    private String correo;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "rol", columnDefinition = "rol_restaurante")
    private RolRestaurante rol;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "pin", length = 6)
    private String pin;

    @Column(name = "expira_en")
    private OffsetDateTime expiraEn;

    @Column(name = "aceptada")
    private Boolean aceptada;

}