package com.hotel.backend.controller;

import com.hotel.backend.dto.reportes.EstadisticasGeneralesDTO;
import com.hotel.backend.dto.reportes.ActividadRecienteDTO;
import com.hotel.backend.dto.reportes.ResumenFinancieroDTO;
import com.hotel.backend.service.ReportesService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReportesService reportesService;

    @GetMapping("/estadisticas-generales")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasGenerales() {
        return ResponseEntity.ok(reportesService.obtenerEstadisticasGenerales());
    }

    @GetMapping("/resumen-financiero")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<ResumenFinancieroDTO> obtenerResumenFinanciero(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reportesService.obtenerResumenFinanciero(fechaInicio, fechaFin));
    }

    @GetMapping("/actividad-reciente")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<ActividadRecienteDTO>> obtenerActividadReciente(
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(reportesService.obtenerActividadReciente(limite));
    }

    @GetMapping("/tasa-ocupacion")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Double> calcularTasaOcupacion() {
        return ResponseEntity.ok(reportesService.calcularTasaOcupacion());
    }

    @GetMapping("/estadisticas-periodo")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<EstadisticasGeneralesDTO> obtenerEstadisticasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(reportesService.obtenerEstadisticasPorPeriodo(fechaInicio, fechaFin));
    }
}