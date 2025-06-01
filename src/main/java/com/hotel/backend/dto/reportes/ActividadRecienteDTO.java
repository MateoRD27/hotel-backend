package com.hotel.backend.dto.reportes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActividadRecienteDTO {
    private String tipo; // RESERVA, PAGO, CHECKIN, MANTENIMIENTO
    private String mensaje;
    private LocalDateTime fecha;
    private String icono; // Para el frontend
}