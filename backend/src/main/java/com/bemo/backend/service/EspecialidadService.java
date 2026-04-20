package com.bemo.backend.service;

import com.bemo.backend.dto.EspecialidadDto;
import com.bemo.backend.model.Especialidad;
import com.bemo.backend.repository.EspecialidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecialidadService {

    private final EspecialidadRepository repo;

    public List<EspecialidadDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public EspecialidadDto create(EspecialidadDto dto) {
        Especialidad e = new Especialidad();
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        return toDto(repo.save(e));
    }

    public EspecialidadDto update(Long id, EspecialidadDto dto) {
        Especialidad e = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Especialidad no encontrada"));
        if (dto.getNombre() != null) e.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) e.setDescripcion(dto.getDescripcion());
        if (dto.getActiva() != null) e.setActiva(dto.getActiva());
        return toDto(repo.save(e));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private EspecialidadDto toDto(Especialidad e) {
        return new EspecialidadDto(e.getId(), e.getNombre(), e.getDescripcion(), e.getActiva());
    }
}
