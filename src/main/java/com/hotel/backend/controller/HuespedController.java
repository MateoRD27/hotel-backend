package com.hotel.backend.controller;

import com.hotel.backend.dto.HuespedDTO;
import com.hotel.backend.service.HuespedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/huespedes")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
public class HuespedController {

    private final HuespedService huespedService;

    // Crear nuevo huésped
    @PostMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<HuespedDTO> registrarHuesped(@Valid @RequestBody HuespedDTO huespedDTO) {
        HuespedDTO creado = huespedService.registrarHuesped(huespedDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado); // 201
    }

    // Actualizar huésped
    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<HuespedDTO> actualizarHuesped(@PathVariable Long id, @Valid @RequestBody HuespedDTO huespedDTO) {
        HuespedDTO actualizado = huespedService.actualizarHuesped(id, huespedDTO);
        return ResponseEntity.ok(actualizado); // 200
    }

    // Eliminar huésped
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<Void> eliminarHuesped(@PathVariable Long id) {
        huespedService.eliminarHuesped(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // Obtener huésped por ID
    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<HuespedDTO> obtenerHuesped(@PathVariable Long id) {
        HuespedDTO huesped = huespedService.obtenerHuespedPorId(id);
        return ResponseEntity.ok(huesped); // 200
    }

    // Listar todos los huéspedes
    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    public ResponseEntity<List<HuespedDTO>> listarHuespedes() {
        List<HuespedDTO> lista = huespedService.listarHuespedes();
        return ResponseEntity.ok(lista); // 200
    }
}
