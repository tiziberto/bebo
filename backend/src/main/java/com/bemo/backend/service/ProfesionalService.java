package com.bemo.backend.service;

import com.bemo.backend.dto.PageResponse;
import com.bemo.backend.dto.ProfesionalDto;
import com.bemo.backend.model.*;
import com.bemo.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfesionalService {

    private final ProfesionalRepository repo;
    private final ObraSocialRepository osRepo;
    private final EstudioRepository estudioRepo;
    private final SucursalRepository sucursalRepo;
    private final EspecialidadRepository especialidadRepo;

    public PageResponse<ProfesionalDto> getPage(String query, int page, int size) {
        List<ProfesionalDto> all;
        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            all = repo.findAll().stream()
                .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(q))
                          || (p.getMatricula() != null && p.getMatricula().toLowerCase().contains(q)))
                .map(this::toDto)
                .collect(Collectors.toList());
        } else {
            all = repo.findAll(Sort.by("nombre")).stream().map(this::toDto).collect(Collectors.toList());
        }
        int total = all.size();
        int from = Math.min(page * size, total);
        int to   = Math.min(from + size, total);
        List<ProfesionalDto> slice = all.subList(from, to);
        PageRequest pr = PageRequest.of(page, size);
        return PageResponse.of(new PageImpl<>(slice, pr, total));
    }

    public List<ProfesionalDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<ProfesionalDto> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.trim().toLowerCase();
        return repo.findAll().stream()
            .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(q)
                      || p.getMatricula() != null && p.getMatricula().toLowerCase().contains(q))
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public List<ProfesionalDto> getBySucursal(Long sucursalId) {
        return repo.findBySucursalId(sucursalId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ProfesionalDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
    }

    public ProfesionalDto getByUserId(Long userId) {
        throw new RuntimeException("Asociación usuario-profesional no disponible en el schema actual");
    }

    @Transactional
    public ProfesionalDto create(ProfesionalDto dto) {
        Profesional p = new Profesional();
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    @Transactional
    public ProfesionalDto update(Long id, ProfesionalDto dto) {
        Profesional p = repo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    public void delete(Long id) {
        Profesional p = repo.findById(id).orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
        p.setFechaBaja(LocalDate.now());
        repo.save(p);
    }

    private void mapFromDto(Profesional p, ProfesionalDto dto) {
        if (dto.getNombre() != null) p.setNombre(dto.getNombre());
        if (dto.getMatricula() != null) p.setMatricula(dto.getMatricula());
        if (dto.getTelefono() != null) p.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) p.setEmail(dto.getEmail());

        if (dto.getEspecialidadesIds() != null) {
            p.setEspecialidades(new HashSet<>(especialidadRepo.findAllById(dto.getEspecialidadesIds())));
        }
        if (dto.getObrasSocialesIds() != null) {
            p.setObrasSociales(new HashSet<>(osRepo.findAllById(dto.getObrasSocialesIds())));
        }
        if (dto.getEstudiosIds() != null) {
            p.setEstudios(new HashSet<>(estudioRepo.findAllById(dto.getEstudiosIds())));
        }
        if (dto.getSucursalesIds() != null) {
            p.setSucursales(new HashSet<>(sucursalRepo.findAllById(dto.getSucursalesIds())));
        }
    }

    private ProfesionalDto toDto(Profesional p) {
        return new ProfesionalDto(
            p.getId(), p.getNombre(), null, null, p.getMatricula(),
            p.getTelefono(), p.getEmail(),
            p.getEspecialidades().stream().map(Especialidad::getId).collect(Collectors.toSet()),
            p.getObrasSociales().stream().map(ObraSocial::getId).collect(Collectors.toSet()),
            p.getEstudios().stream().map(Estudio::getId).collect(Collectors.toSet()),
            p.getSucursales().stream().map(Sucursal::getId).collect(Collectors.toSet()),
            null,
            true
        );
    }
}
