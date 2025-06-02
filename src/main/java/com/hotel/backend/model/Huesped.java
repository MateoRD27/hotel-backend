package com.hotel.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.*;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "huespedes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Huesped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    private String apellido;

    @Column(unique = true)
    @NotBlank
    private String email;

    @NotBlank
    private String telefono;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String direccion;

    @NotBlank
    private String ciudad;

    @NotBlank
    private String pais;

    @Column(name = "documento_identidad")
    @NotBlank
    private String documentoIdentidad;

    @Column(name = "tipo_documento")
    @NotBlank
    private String tipoDocumento;

    private String notas;

    // Relación con reservaciones
    @OneToMany(mappedBy = "huesped", cascade = CascadeType.ALL)
    private List<Reserva> reservaciones = new ArrayList<>();
}