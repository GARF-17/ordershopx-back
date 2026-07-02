package com.ordershopx.backend.modules.onboarding.entity;

import com.ordershopx.backend.shared.entity.BaseCreacionEntity;
import com.ordershopx.backend.shared.enums.EstadoInvitacion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitaciones_restaurante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitacionRestaurante extends BaseCreacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_invitacion", updatable = false, nullable = false)
    private UUID idInvitacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud")
    private SolicitudRestaurante solicitud;

    @Column(name = "token", length = 255, unique = true, nullable = false)
    private String token;

    @Column(name = "pin", length = 6, nullable = false)
    private String pin;

    @Column(name = "expira_en", nullable = false)
    private OffsetDateTime expiraEn;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", columnDefinition = "estado_invitacion")
    private EstadoInvitacion estado;

    @Column(name = "usado_en")
    private OffsetDateTime usadoEn;

}