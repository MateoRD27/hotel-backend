package com.hotel.backend.repository;

import com.hotel.backend.model.Habitacion;
import com.hotel.backend.enums.EstadoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    List<Habitacion> findByEstado(EstadoHabitacion estado);
    boolean existsByNumero(String numero);
    long countByEstado(EstadoHabitacion estado);

    @Query("SELECT h.id, h.numero, 1 FROM Habitacion h WHERE h.estado = 'MANTENIMIENTO'")
    List<Object[]> findHabitacionesMantenimiento(Pageable pageable);
}
