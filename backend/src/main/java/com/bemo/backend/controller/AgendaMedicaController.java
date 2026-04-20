package com.bemo.backend.controller;

import com.bemo.backend.dto.AgendaMedicaDto;
import com.bemo.backend.service.AgendaMedicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agenda")
@RequiredArgsConstructor
public class AgendaMedicaController {

    private final AgendaMedicaService service;

    @GetMapping
    public ResponseEntity<List<AgendaMedicaDto>> getAll(
            @RequestParam(required = false) Long profesionalId,
            @RequestParam(required = false) Long sucursalId) {
        if (profesionalId != null && sucursalId != null) {
            return ResponseEntity.ok(service.getByProfesionalAndSucursal(profesionalId, sucursalId));
        }
        if (profesionalId != null) {
            return ResponseEntity.ok(service.getByProfesional(profesionalId));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> create(@RequestBody AgendaMedicaDto dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AgendaMedicaDto dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("message", "Agenda eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
