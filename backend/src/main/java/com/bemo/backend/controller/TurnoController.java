package com.bemo.backend.controller;

import com.bemo.backend.dto.TurnoDto;
import com.bemo.backend.model.Turno;
import com.bemo.backend.repository.TurnoRepository;
import com.bemo.backend.service.EmailService;
import com.bemo.backend.service.TurnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService service;
    private final TurnoRepository turnoRepo;
    private final EmailService emailService;

    @GetMapping
    public ResponseEntity<List<TurnoDto>> getAll(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) Long profesionalId,
            @RequestParam(required = false) Long sucursalId,
            @RequestParam(required = false) Long pacienteId) {

        if (pacienteId != null) return ResponseEntity.ok(service.getByPaciente(pacienteId));

        // Rango de fechas (para calendario semana/mes)
        if (desde != null && hasta != null) {
            return ResponseEntity.ok(service.getByRango(desde, hasta, profesionalId));
        }

        String f = fecha != null ? fecha : LocalDate.now().toString();
        if (profesionalId != null) return ResponseEntity.ok(service.getByProfesionalAndFecha(profesionalId, f));
        if (sucursalId != null) return ResponseEntity.ok(service.getBySucursalAndFecha(sucursalId, f));
        return ResponseEntity.ok(service.getByFecha(f));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> create(@RequestBody TurnoDto dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody TurnoDto dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION', 'DOCTOR')")
    public ResponseEntity<?> updateEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.updateEstado(id, body.get("estado")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reprogramar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> reprogramar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(service.reprogramar(id, body.get("fechaHora")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.cancelar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("message", "Turno eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Envía email de confirmación al paciente del turno */
    @PostMapping("/{id}/enviar-confirmacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> enviarConfirmacion(@PathVariable Long id) {
        try {
            Turno turno = turnoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            if (turno.getPaciente().getEmail() == null || turno.getPaciente().getEmail().isBlank()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El paciente no tiene email registrado"));
            }

            emailService.enviarConfirmacionTurno(turno);

            // Registrar timestamp de envío
            turno.setConfirmacionEnviadaAt(LocalDateTime.now());
            turnoRepo.save(turno);

            return ResponseEntity.ok(Map.of(
                "message", "Confirmación enviada a " + turno.getPaciente().getEmail(),
                "turno", service.toDto(turno)
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
