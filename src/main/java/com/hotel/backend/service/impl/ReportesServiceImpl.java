package com.hotel.backend.service.impl;

import com.hotel.backend.dto.reportes.EstadisticasGeneralesDTO;
import com.hotel.backend.dto.reportes.ActividadRecienteDTO;
import com.hotel.backend.dto.reportes.ResumenFinancieroDTO;
import com.hotel.backend.enums.EstadoHabitacion;
import com.hotel.backend.enums.EstadoReserva;
import com.hotel.backend.enums.EstadoPago;
import com.hotel.backend.repository.*;
import com.hotel.backend.service.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportesServiceImpl implements ReportesService {

    private final HabitacionRepository habitacionRepository;
    private final ReservaRepository reservaRepository;
    private final FacturaRepository facturaRepository;
    private final HuespedRepository huespedRepository;
    private final PagoRepository pagoRepository;

    public EstadisticasGeneralesDTO obtenerEstadisticasGenerales() {
        EstadisticasGeneralesDTO estadisticas = new EstadisticasGeneralesDTO();

        // Estadísticas de habitaciones
        estadisticas.setTotalHabitaciones(habitacionRepository.count());
        estadisticas.setHabitacionesDisponibles(habitacionRepository.countByEstado(EstadoHabitacion.DISPONIBLE));
        estadisticas.setHabitacionesOcupadas(habitacionRepository.countByEstado(EstadoHabitacion.OCUPADA));
        estadisticas.setHabitacionesMantenimiento(habitacionRepository.countByEstado(EstadoHabitacion.MANTENIMIENTO));

        // Estadísticas de reservas
        estadisticas.setTotalReservas(reservaRepository.count());
        estadisticas.setReservasPendientes(reservaRepository.countByEstadoReserva(EstadoReserva.CONFIRMADA));
        estadisticas.setReservasConfirmadas(reservaRepository.countByEstadoReserva(EstadoReserva.CONFIRMADA));
        estadisticas.setReservasCanceladas(reservaRepository.countByEstadoReserva(EstadoReserva.CANCELADA));

        // Estadísticas de facturas
        estadisticas.setTotalFacturas(facturaRepository.count());
        estadisticas.setFacturasPendientes(facturaRepository.countByEstadoPago(EstadoPago.PENDIENTE));
        estadisticas.setFacturasPagadas(facturaRepository.countByEstadoPago(EstadoPago.PAGADA));
        estadisticas.setFacturasVencidas(facturaRepository.countByEstadoPagoAndFechaVencimientoBefore(
                EstadoPago.PENDIENTE, LocalDate.now()));

        // Estadísticas de huéspedes
        estadisticas.setTotalHuespedes(huespedRepository.count());
        estadisticas.setHuespedesActivos(reservaRepository.countDistinctHuespedByEstadoReserva(EstadoReserva.EN_CURSO));

        return estadisticas;
    }

    public ResumenFinancieroDTO obtenerResumenFinanciero(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1); // Primer día del mes actual
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now(); // Día actual
        }

        ResumenFinancieroDTO resumen = new ResumenFinancieroDTO();

        // Ingresos del período
        BigDecimal ingresosPeriodo = pagoRepository.sumMontoByFechaPagoBetween(
                fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));
        resumen.setIngresosPeriodo(ingresosPeriodo != null ? ingresosPeriodo : BigDecimal.ZERO);

        // Facturas pendientes
        BigDecimal facturasPendientes = facturaRepository.sumTotalByEstadoPago(EstadoPago.PENDIENTE);
        resumen.setFacturasPendientes(facturasPendientes != null ? facturasPendientes : BigDecimal.ZERO);

        // Facturas vencidas
        BigDecimal facturasVencidas = facturaRepository.sumTotalByEstadoPagoAndFechaVencimientoBefore(
                EstadoPago.PENDIENTE, LocalDate.now());
        resumen.setFacturasVencidas(facturasVencidas != null ? facturasVencidas : BigDecimal.ZERO);

        // Ingresos totales pagadas
        BigDecimal totalPagadas = facturaRepository.sumTotalByEstadoPago(EstadoPago.PAGADA);
        resumen.setTotalPagadas(totalPagadas != null ? totalPagadas : BigDecimal.ZERO);

        return resumen;
    }

    public List<ActividadRecienteDTO> obtenerActividadReciente(int limite) {
        List<ActividadRecienteDTO> actividades = new ArrayList<>();

        // Últimas reservas
        List<Object[]> ultimasReservas = reservaRepository.findUltimasReservas(PageRequest.of(0, limite/4));
        for (Object[] reserva : ultimasReservas) {
            ActividadRecienteDTO actividad = new ActividadRecienteDTO();
            actividad.setTipo("RESERVA");
            actividad.setMensaje("Nueva reserva - Habitación " + reserva[1]);
            actividad.setFecha((LocalDateTime) reserva[2]);
            actividad.setIcono("Calendar");
            actividades.add(actividad);
        }

        // Últimos pagos
        List<Object[]> ultimosPagos = pagoRepository.findUltimosPagos(PageRequest.of(0, limite/4));
        for (Object[] pago : ultimosPagos) {
            ActividadRecienteDTO actividad = new ActividadRecienteDTO();
            actividad.setTipo("PAGO");
            actividad.setMensaje("Pago recibido - " + pago[1] + " - $" + pago[2]);
            actividad.setFecha((LocalDateTime) pago[3]);
            actividad.setIcono("CreditCard");
            actividades.add(actividad);
        }

        // Últimos check-ins
        List<Object[]> ultimosCheckins = reservaRepository.findUltimosCheckins(PageRequest.of(0, limite/4));
        for (Object[] checkin : ultimosCheckins) {
            ActividadRecienteDTO actividad = new ActividadRecienteDTO();
            actividad.setTipo("CHECKIN");
            actividad.setMensaje("Check-in completado - " + checkin[1]);
            actividad.setFecha((LocalDateTime) checkin[2]);
            actividad.setIcono("Users");
            actividades.add(actividad);
        }

        // Habitaciones en mantenimiento
        List<Object[]> mantenimientos = habitacionRepository.findHabitacionesMantenimiento(PageRequest.of(0, limite/4));
        for (Object[] mant : mantenimientos) {
            ActividadRecienteDTO actividad = new ActividadRecienteDTO();
            actividad.setTipo("MANTENIMIENTO");
            actividad.setMensaje("Habitación " + mant[1] + " en mantenimiento");
            actividad.setFecha(LocalDateTime.now().minusHours((Long) mant[2])); // Simulado
            actividad.setIcono("AlertCircle");
            actividades.add(actividad);
        }

        // Ordenar por fecha descendente y limitar
        return actividades.stream()
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .limit(limite)
                .toList();
    }

    public Double calcularTasaOcupacion() {
        long totalHabitaciones = habitacionRepository.count();
        long habitacionesOcupadas = habitacionRepository.countByEstado(EstadoHabitacion.OCUPADA);

        if (totalHabitaciones == 0) return 0.0;

        return (double) habitacionesOcupadas / totalHabitaciones * 100;
    }

    public EstadisticasGeneralesDTO obtenerEstadisticasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        EstadisticasGeneralesDTO estadisticas = new EstadisticasGeneralesDTO();

        // Habitaciones actuales
        estadisticas.setTotalHabitaciones(habitacionRepository.count());
        estadisticas.setHabitacionesDisponibles(habitacionRepository.countByEstado(EstadoHabitacion.DISPONIBLE));
        estadisticas.setHabitacionesOcupadas(habitacionRepository.countByEstado(EstadoHabitacion.OCUPADA));
        estadisticas.setHabitacionesMantenimiento(habitacionRepository.countByEstado(EstadoHabitacion.MANTENIMIENTO));

        // Reservas en el período
        estadisticas.setTotalReservas(reservaRepository.countByFechaReservaBetween(
                fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59)));
        estadisticas.setReservasConfirmadas(reservaRepository.countByEstadoReservaAndFechaReservaBetween(
                EstadoReserva.CONFIRMADA, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59)));
        estadisticas.setReservasCanceladas(reservaRepository.countByEstadoReservaAndFechaReservaBetween(
                EstadoReserva.CANCELADA, fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59)));

        // Inicializa campos aún no calculados para evitar null en el JSON
        estadisticas.setReservasPendientes(0L);

        // Facturas en el período
        estadisticas.setTotalFacturas(facturaRepository.countByFechaEmisionBetween(fechaInicio, fechaFin));
        estadisticas.setFacturasPagadas(facturaRepository.countByEstadoPagoAndFechaEmisionBetween(
                EstadoPago.PAGADA, fechaInicio, fechaFin));

        estadisticas.setFacturasPendientes(0L);
        estadisticas.setFacturasVencidas(0L);

        // Huéspedes
        estadisticas.setTotalHuespedes(0L);
        estadisticas.setHuespedesActivos(0L);

        return estadisticas;
    }

}