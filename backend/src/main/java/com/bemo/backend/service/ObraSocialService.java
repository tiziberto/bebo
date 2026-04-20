package com.bemo.backend.service;

import com.bemo.backend.dto.ObraSocialDto;
import com.bemo.backend.dto.PageResponse;
import com.bemo.backend.dto.PlanDto;
import com.bemo.backend.model.ObraSocial;
import com.bemo.backend.model.Plan;
import com.bemo.backend.repository.ObraSocialRepository;
import com.bemo.backend.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObraSocialService {

    private final ObraSocialRepository osRepo;
    private final PlanRepository planRepo;

    @Transactional(readOnly = true)
    public PageResponse<ObraSocialDto> getPage(String query, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("nombre"));
        if (query == null || query.isBlank()) {
            return PageResponse.of(osRepo.findAll(pr).map(this::toDto));
        }
        return PageResponse.of(osRepo.searchPage(query.trim(), pr).map(this::toDto));
    }

    @Transactional(readOnly = true)
    public List<ObraSocialDto> getAll() {
        return osRepo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public ObraSocialDto create(ObraSocialDto dto) {
        ObraSocial os = new ObraSocial();
        os.setNombre(dto.getNombre());
        os.setCodigo(dto.getCodigo());
        os.setTelefono(dto.getTelefono());
        os.setEmail(dto.getEmail());
        os.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        return toDto(osRepo.save(os));
    }

    public ObraSocialDto update(Long id, ObraSocialDto dto) {
        ObraSocial os = osRepo.findById(id).orElseThrow(() -> new RuntimeException("Obra Social no encontrada"));
        if (dto.getNombre() != null) os.setNombre(dto.getNombre());
        if (dto.getCodigo() != null) os.setCodigo(dto.getCodigo());
        if (dto.getTelefono() != null) os.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) os.setEmail(dto.getEmail());
        if (dto.getActiva() != null) os.setActiva(dto.getActiva());
        return toDto(osRepo.save(os));
    }

    public void delete(Long id) {
        osRepo.deleteById(id);
    }

    // --- Planes ---

    public List<PlanDto> getPlanes(Long obraSocialId) {
        return planRepo.findByObraSocialId(obraSocialId).stream().map(this::planToDto).collect(Collectors.toList());
    }

    public PlanDto createPlan(Long obraSocialId, PlanDto dto) {
        ObraSocial os = osRepo.findById(obraSocialId).orElseThrow(() -> new RuntimeException("Obra Social no encontrada"));
        Plan plan = new Plan();
        plan.setNombre(dto.getNombre());
        plan.setObraSocial(os);
        plan.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        return planToDto(planRepo.save(plan));
    }

    public PlanDto updatePlan(Long planId, PlanDto dto) {
        Plan plan = planRepo.findById(planId).orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (dto.getNombre() != null) plan.setNombre(dto.getNombre());
        if (dto.getActivo() != null) plan.setActivo(dto.getActivo());
        return planToDto(planRepo.save(plan));
    }

    public void deletePlan(Long planId) {
        planRepo.deleteById(planId);
    }

    private ObraSocialDto toDto(ObraSocial os) {
        List<PlanDto> planes = os.getPlanes() != null
            ? os.getPlanes().stream().map(this::planToDto).collect(Collectors.toList())
            : new ArrayList<>();
        return new ObraSocialDto(os.getId(), os.getNombre(), os.getCodigo(),
            os.getTelefono(), os.getEmail(), os.getActiva(), planes);
    }

    private PlanDto planToDto(Plan p) {
        return new PlanDto(p.getId(), p.getNombre(),
            p.getObraSocial().getId(), p.getObraSocial().getNombre(), p.getActivo());
    }
}
