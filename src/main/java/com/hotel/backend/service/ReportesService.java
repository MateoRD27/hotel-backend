package com.hotel.backend.service;

import com.hotel.backend.dto.reportes.ActividadRecienteDTO;
import com.hotel.backend.dto.reportes.EstadisticasGeneralesDTO;
import com.hotel.backend.dto.reportes.ResumenFinancieroDTO;

import java.time.LocalDate;

import java.util.List;

public interface ReportesService {
    EstadisticasGeneralesDTO obtenerEstadisticasGenerales();

    ResumenFinancieroDTO obtenerResumenFinanciero(LocalDate fechaInicio, LocalDate fechaFin);

    List<ActividadRecienteDTO> obtenerActividadReciente(int limite);

    Double calcularTasaOcupacion();

    EstadisticasGeneralesDTO obtenerEstadisticasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

}