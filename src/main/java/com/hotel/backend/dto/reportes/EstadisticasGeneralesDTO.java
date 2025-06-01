package com.hotel.backend.dto.reportes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasGeneralesDTO {
    // Estadísticas de habitaciones
    private Long totalHabitaciones;
    private Long habitacionesDisponibles;
    private Long habitacionesOcupadas;
    private Long habitacionesMantenimiento;

    // Estadísticas de reservas
    private Long totalReservas;
    private Long reservasPendientes;
    private Long reservasConfirmadas;
    private Long reservasCanceladas;

    // Estadísticas de facturas
    private Long totalFacturas;
    private Long facturasPendientes;
    private Long facturasPagadas;
    private Long facturasVencidas;

    // Estadísticas de huéspedes
    private Long totalHuespedes;
    private Long huespedesActivos;
}
