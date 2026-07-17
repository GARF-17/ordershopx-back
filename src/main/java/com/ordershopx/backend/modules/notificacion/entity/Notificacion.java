package com.ordershopx.backend.modules.notificacion.entity;

import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.modules.usuario.entity.Usuario;
import com.ordershopx.backend.shared.entity.BaseCreacionEntity;
import com.ordershopx.backend.shared.enums.TipoNotificacion;
import com.ordershopx.backend.shared.enums.RolGlobal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "notificaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion extends BaseCreacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_notificacion")
    private UUID idNotificacion;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    @Column(name = "mensaje", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @NotNull(message = "El tipo de notificación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, columnDefinition = "tipo_notificacion")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private TipoNotificacion tipo;

    @NotNull(message = "El rol destinatario es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "rol_destinatario", nullable = false, columnDefinition = "rol_global")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private RolGlobal rolDestinatario;

    @Builder.Default
    @Column(name = "leida")
    private Boolean leida = false;
}