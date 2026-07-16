package com.ordershopx.backend.modules.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets_soporte")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketSoporte {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_ticket", updatable = false, nullable = false)
    private UUID idTicket;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 50)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(nullable = false, length = 150)
    private String asunto; // Título corto (Ej: "Problema con el mapa")

    @Column(nullable = false, length = 1000)
    private String descripcion; // Detalle largo desplegable

    @Column(name = "correo_usuario", nullable = false, length = 150)
    private String correoUsuario;

    @Column(name = "rol_usuario", nullable = false, length = 50)
    private String rolUsuario;

    @Column(name = "respuesta_admin", length = 1000)
    private String respuestaAdmin; // Lo que el admin escribe para resolver
}