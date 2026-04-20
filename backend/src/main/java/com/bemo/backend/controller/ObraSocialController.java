package com.bemo.backend.controller;

import com.bemo.backend.dto.ObraSocialDto;
import com.bemo.backend.dto.PageResponse;
import com.bemo.backend.dto.PlanDto;
import com.bemo.backend.service.ObraSocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/obras-sociales")
@RequiredArgsConstructor
public class ObraSocialController {

    private final ObraSocialService service;

    @GetMapping
    public ResponseEntity<PageResponse<ObraSocialDto>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.getPage(q, page, size));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> create(@RequestBody ObraSocialDto dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ObraSocialDto dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(Map.of("message", "Obra Social eliminada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- Planes ---

    @GetMapping("/{id}/planes")
    public ResponseEntity<List<PlanDto>> getPlanes(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPlanes(id));
    }

    @PostMapping("/{id}/planes")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> createPlan(@PathVariable Long id, @RequestBody PlanDto dto) {
        try {
            return ResponseEntity.ok(service.createPlan(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/planes/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public ResponseEntity<?> updatePlan(@PathVariable Long planId, @RequestBody PlanDto dto) {
        try {
            return ResponseEntity.ok(service.updatePlan(planId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/planes/{planId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deletePlan(@PathVariable Long planId) {
        try {
            service.deletePlan(planId);
            return ResponseEntity.ok(Map.of("message", "Plan eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
