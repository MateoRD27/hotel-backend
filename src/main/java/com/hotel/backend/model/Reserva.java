package com.hotel.backend.model;

import com.hotel.backend.enums.EstadoReserva;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_reserva")
    private LocalDateTime fechaReserva;

    @Column(name = "fecha_checkin")
    private LocalDate fechaCheckin;

    @Column(name = "fecha_checkout")
    private LocalDate fechaCheckout;

    @Column(name = "num_personas")
    @Positive
    private Integer numPersonas;

    private String notas;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva;

    // Relación con factura
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Factura factura;

    //Relación con habitacion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id", nullable = false)
    private Habitacion habitacion;

    //Relación con huesped
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "huesped_id", nullable = false)
    private Huesped huesped;

    //Relación con usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}