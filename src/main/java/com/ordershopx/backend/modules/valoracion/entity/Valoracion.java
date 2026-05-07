package com.ordershopx.backend.modules.valoracion.entity;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;

import com.ordershopx.backend.shared.entity.BaseCreacionEntity;
import jakarta.persistence.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "valoracion",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_valoracion_pedido",
                        columnNames = "id_pedido"
                )
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Valoracion extends BaseCreacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_valoracion", updatable = false, nullable = false)
    private UUID idValoracion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false, foreignKey = @ForeignKey(name = "fk_valoracion_pedido"))
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false, foreignKey = @ForeignKey(name = "fk_valoracion_cliente"))
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_restaurante", nullable = false, foreignKey = @ForeignKey(name = "fk_valoracion_restaurante"))
    private Restaurante restaurante;

    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;
}