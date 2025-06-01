package com.hotel.backend.repository;

import com.hotel.backend.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hotel.backend.enums.EstadoPago;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    boolean existsByNumeroFactura(String numeroFactura);

    // consultas para reportes
    long countByEstadoPago(EstadoPago estado);

    long countByEstadoPagoAndFechaVencimientoBefore(EstadoPago estado, LocalDate fecha);

    long countByFechaEmisionBetween(LocalDate inicio, LocalDate fin);

    long countByEstadoPagoAndFechaEmisionBetween(EstadoPago estado, LocalDate inicio, LocalDate fin);

    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f WHERE f.estadoPago = :estado")
    BigDecimal sumTotalByEstadoPago(@Param("estado") EstadoPago estado);

    @Query("SELECT COALESCE(SUM(f.total), 0) FROM Factura f WHERE f.estadoPago = :estado AND f.fechaVencimiento < :fecha")
    BigDecimal sumTotalByEstadoPagoAndFechaVencimientoBefore(@Param("estado") EstadoPago estado, @Param("fecha") LocalDate fecha);
}
