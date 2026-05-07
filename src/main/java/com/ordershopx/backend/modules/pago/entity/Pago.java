package com.ordershopx.backend.modules.pago.entity;

import com.ordershopx.backend.modules.pedido.entity.Pedido;
import com.ordershopx.backend.shared.enums.MetodoPago;
import com.ordershopx.backend.shared.enums.TipoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "pagos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_pedido_tipo",
                        columnNames = {"id_pedido", "tipo_pago"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_pago", nullable = false, updatable = false)
    private UUID idPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @NotNull(message = "El tipo de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tipo_pago", nullable = false, columnDefinition = "tipo_pago")
    private TipoPago tipoPago;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "metodo_pago", nullable = false, columnDefinition = "metodo_pago")
    private MetodoPago metodoPago;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "Formato de monto inválido")
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Builder.Default
    @NotBlank(message = "La moneda es obligatoria")
    @Size(max = 10)
    @Column(name = "moneda", length = 10)
    private String moneda = "PEN";

    @Size(max = 100)
    @Column(name = "numero_operacion", length = 100)
    private String numeroOperacion;

    @Builder.Default
    @Column(name = "es_confirmado")
    private Boolean esConfirmado = true;

    @CreationTimestamp
    @Column(name = "fecha_procesamiento", nullable = false, updatable = false)
    private OffsetDateTime fechaProcesamiento;
}