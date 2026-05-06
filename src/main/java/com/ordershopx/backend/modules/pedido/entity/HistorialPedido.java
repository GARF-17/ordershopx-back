package com.ordershopx.backend.modules.pedido.entity;

import com.ordershopx.backend.shared.enums.EstadoPedido;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_historial")
    private UUID idHistorial;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "estado_pedido")
    private EstadoPedido estado;

    @Column(name = "fecha_cambio")
    private OffsetDateTime fechaCambio;
}