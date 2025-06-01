package com.hotel.backend.service.impl;

import com.hotel.backend.dto.ReservaDTO;
import com.hotel.backend.dto.mapper.ReservaMapper;
import com.hotel.backend.enums.EstadoHabitacion;
import com.hotel.backend.enums.EstadoReserva;
import com.hotel.backend.exception.HabitacionNoDisponible;
import com.hotel.backend.exception.ProblemaRealizarCheckIn;
import com.hotel.backend.exception.ReservaYaExiste;
import com.hotel.backend.exception.ResourceNotFoundException;
import com.hotel.backend.model.Habitacion;
import com.hotel.backend.model.Huesped;
import com.hotel.backend.model.Reserva;
import com.hotel.backend.model.Usuario;
import com.hotel.backend.repository.HabitacionRepository;
import com.hotel.backend.repository.HuespedRepository;
import com.hotel.backend.repository.ReservaRepository;
import com.hotel.backend.repository.UsuarioRepository;
import com.hotel.backend.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {
    private final ReservaRepository reservaRepository;
    private final HabitacionRepository habitacionRepository;
    private final HuespedRepository huespedRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaMapper reservaMapper;


    @Override
    public ReservaDTO crearReserva(ReservaDTO dto) {
        Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar estado de la habitación
        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE) {
            throw new HabitacionNoDisponible("La habitación no está disponible para reservar.");
        }

        // Verificar conflictos de fechas
        List<Reserva> conflictos = reservaRepository.findReservasEnConflicto(
                habitacion.getId(),
                dto.getFechaCheckin(),
                dto.getFechaCheckout()
        );

        if (!conflictos.isEmpty()) {
            throw new ReservaYaExiste("Ya existe una reserva para esta habitación en las fechas seleccionadas.");
        }

        Huesped huesped = huespedRepository.findById(dto.getHuespedId())
                .orElseThrow(() -> new ResourceNotFoundException("Huésped no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Reserva reserva = Reserva.builder()
                .fechaReserva(dto.getFechaReserva())
                .fechaCheckin(dto.getFechaCheckin())
                .fechaCheckout(dto.getFechaCheckout())
                .numPersonas(dto.getNumPersonas())
                .notas(dto.getNotas())
                .estadoReserva(dto.getEstadoReserva())
                .habitacion(habitacion)
                .huesped(huesped)
                .usuario(usuario)
                .build();

        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

    @Override
    public ReservaDTO actualizarReserva(Long id, ReservaDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        Habitacion habitacion = habitacionRepository.findById(dto.getHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada"));

        // Validar estado de la habitación
        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE){
            throw new HabitacionNoDisponible("La habitación no está disponible para reservar");
        }

        // Validar conflictos de reserva, excluyendo la actual
        List<Reserva> conflictos = reservaRepository.findReservasEnConflicto(
                dto.getHabitacionId(),
                dto.getFechaCheckin(),
                dto.getFechaCheckout()
        ).stream().filter(r -> !r.getId().equals(id)).toList();

        if (!conflictos.isEmpty()) {
            throw new ReservaYaExiste("Ya existe una reserva en ese rango de fechas");
        }

        Huesped huesped = huespedRepository.findById(dto.getHuespedId())
                .orElseThrow(() -> new ResourceNotFoundException("Huésped no encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Actualizar campos
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setFechaCheckin(dto.getFechaCheckin());
        reserva.setFechaCheckout(dto.getFechaCheckout());
        reserva.setNumPersonas(dto.getNumPersonas());
        reserva.setNotas(dto.getNotas());
        reserva.setEstadoReserva(dto.getEstadoReserva());
        reserva.setHabitacion(habitacion);
        reserva.setHuesped(huesped);
        reserva.setUsuario(usuario);

        return reservaMapper.toDTO(reservaRepository.save(reserva));
    }

    @Override
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    public void eliminarReserva(Long id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva no encontrada con ID: " + id);
        }
        reservaRepository.deleteById(id);
    }

    @Override
    public ReservaDTO obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .map(reservaMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
    }

    @Override
    public List<ReservaDTO> listarReservas() {
        return reservaRepository.findAll().stream()
                .map(reservaMapper::toDTO)
                .toList();
    }

    @Override
    public List<ReservaDTO> listarReservasPorUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }

        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(reservaMapper::toDTO)
                .toList();
    }

    @Override
    public void realizarCheckIn(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + reservaId));

        if (reserva.getEstadoReserva() != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("No se puede hacer check-in. Estado actual: " + reserva.getEstadoReserva());
        }

        Habitacion habitacion = reserva.getHabitacion();
        if (habitacion.getEstado() != EstadoHabitacion.DISPONIBLE) {
            throw new HabitacionNoDisponible("La habitación no está disponible para check-in.");
        }

        // Actualizar estados
        reserva.setEstadoReserva(EstadoReserva.EN_CURSO);
        habitacion.setEstado(EstadoHabitacion.OCUPADA);

        reservaRepository.save(reserva);
        habitacionRepository.save(habitacion);
    }

    @Override
    public void realizarCheckOut(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + reservaId));

        if (reserva.getEstadoReserva() != EstadoReserva.EN_CURSO) {
            throw new ProblemaRealizarCheckIn("No se puede hacer check-out. Estado actual: " + reserva.getEstadoReserva());
        }

        Habitacion habitacion = reserva.getHabitacion();

        // Actualizar estados
        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        habitacion.setEstado(EstadoHabitacion.DISPONIBLE);

        reservaRepository.save(reserva);
        habitacionRepository.save(habitacion);
    }
}
