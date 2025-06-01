package com.hotel.backend.controller;

import com.hotel.backend.dto.reportes.EstadisticasGeneralesDTO;
import com.hotel.backend.dto.reportes.ActividadRecienteDTO;
import com.hotel.backend.dto.reportes.ResumenFinancieroDTO;
import com.hotel.backend.service.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final ReportesService reportesService;

    // Obtener estadísticas generales del dashboard
    @GetMapping("/estadisticas-generales")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGenerales() {
        EstadisticasGeneralesDTO estadisticas = reportesService.obtenerEstadisticasGenerales();
        return ResponseEntity.ok(estadisticas);
    }

    // Obtener resumen financiero
    @GetMapping("/resumen-financiero")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<ResumenFinancieroDTO> obtenerResumenFinanciero(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin) {
        ResumenFinancieroDTO resumen = reportesService.obtenerResumenFinanciero(fechaInicio, fechaFin);
        return ResponseEntity.ok(resumen);
    }

    // Obtener actividad reciente
    @GetMapping("/actividad-reciente")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<ActividadRecienteDTO>> obtenerActividadReciente(
            @RequestParam(defaultValue = "10") int limite) {
        List<ActividadRecienteDTO> actividades = reportesService.obtenerActividadReciente(limite);
        return ResponseEntity.ok(actividades);
    }

    // Obtener tasa de ocupación
    @GetMapping("/tasa-ocupacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Double> obtenerTasaOcupacion() {
        double tasaOcupacion = reportesService.calcularTasaOcupacion();
        return ResponseEntity.ok(tasaOcupacion);
    }

    // Reportes más detallados por rango de fechas
    @GetMapping("/estadisticas-periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasPorPeriodo(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        EstadisticasGeneralesDTO estadisticas = reportesService.obtenerEstadisticasPorPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(estadisticas);
    }
}