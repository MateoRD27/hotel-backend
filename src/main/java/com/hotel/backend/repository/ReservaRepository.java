package com.hotel.backend.repository;
import com.hotel.backend.model.Reserva;
import com.hotel.backend.enums.EstadoReserva;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByHuespedId(Long huespedId);
    List<Reserva> findByEstadoReserva(EstadoReserva estado);

    @Query(""" 
    SELECT r FROM Reserva r
    WHERE r.habitacion.id = :habitacionId
    AND (r.fechaCheckin < :fechaCheckout AND r.fechaCheckout > :fechaCheckin)""")
    List<Reserva> findReservasEnConflicto(
            @Param("habitacionId") Long habitacionId,
            @Param("fechaCheckin") LocalDate fechaCheckin,
            @Param("fechaCheckout") LocalDate fechaCheckout
    );
    List<Reserva> findByUsuarioId(Long usuarioId);

    //  consultas para reportes
    long countByEstadoReserva(EstadoReserva estado);

    @Query("SELECT COUNT(DISTINCT r.huesped.id) FROM Reserva r WHERE r.estadoReserva = :estado")
    long countDistinctHuespedByEstadoReserva(@Param("estado") EstadoReserva estado);

    long countByFechaReservaBetween(LocalDateTime inicio, LocalDateTime fin);

    long countByEstadoReservaAndFechaReservaBetween(EstadoReserva estado, LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT r.id, r.habitacion.numero, r.fechaReserva FROM Reserva r ORDER BY r.fechaReserva DESC")
    List<Object[]> findUltimasReservas(Pageable pageable);

    @Query("SELECT r.id, r.huesped.nombre, r.fechaCheckin FROM Reserva r WHERE r.estadoReserva = 'ACTIVA' ORDER BY r.fechaCheckin DESC")
    List<Object[]> findUltimosCheckins(Pageable pageable);

}
