package com.hotel.backend.enums;

public enum EstadoReserva {
    CONFIRMADA,    // Antes del check-in
    EN_CURSO,     // Durante la estadía
    FINALIZADA,   // Después del check-out
    CANCELADA     // Si el cliente canceló
}
