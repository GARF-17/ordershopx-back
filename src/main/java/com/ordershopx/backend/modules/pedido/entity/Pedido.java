package com.ordershopx.backend.modules.pedido.entity;

import com.ordershopx.backend.modules.cliente.entity.Cliente;
import com.ordershopx.backend.modules.restaurante.entity.Restaurante;
import com.ordershopx.backend.shared.entity.BaseEntity;
import com.ordershopx.backend.shared.enums.EstadoPedido;
import com.ordershopx.backend.shared.enums.EstadoPagoGlobal;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido extends BaseEntity {

    @Id
    @Column(name = "id_pedido", nullable = false, updatable = false)
    private UUID idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_restaurante", nullable = false)
    private Restaurante restaurante;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistorialPedido> historial = new ArrayList<>();

    @Column(name = "codigo_recojo", nullable = false, unique = true, length = 6)
    private String codigoRecojo;

    @Column(name = "orden_cola", insertable = false, updatable = false)
    private Integer ordenCola;

    @Column(name = "tiempo_estimado_min", insertable = false, updatable = false)
    private Integer tiempoEstimadoMin;

    @Column(name = "hora_estimada_recojo", insertable = false, updatable = false)
    private OffsetDateTime horaEstimadaRecojo;

    @Column(name = "hora_real_recojo")
    private OffsetDateTime horaRealRecojo;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "estado", nullable = false, columnDefinition = "estado_pedido")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoPedido estado;

    @Column(name = "estado_pago", columnDefinition = "estado_pago_global")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private EstadoPagoGlobal estadoPago;

    @Column(name = "notas_cliente")
    private String notasCliente;

    @Column(name = "horario_recojo_seleccionado")
    private OffsetDateTime horarioRecojoSeleccionado;

    public void addDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void addHistorial(HistorialPedido h) {
        historial.add(h);
        h.setPedido(this);
    }
}