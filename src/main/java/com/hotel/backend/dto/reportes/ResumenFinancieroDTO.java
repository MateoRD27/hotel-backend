package com.hotel.backend.dto.reportes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenFinancieroDTO {
    private BigDecimal ingresosPeriodo;
    private BigDecimal facturasPendientes;
    private BigDecimal facturasVencidas;
    private BigDecimal totalPagadas;
}