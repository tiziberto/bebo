package com.bemo.backend.service;

import com.bemo.backend.dto.SucursalDto;
import com.bemo.backend.model.Sucursal;
import com.bemo.backend.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository repo;

    public List<SucursalDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SucursalDto> getActivas() {
        return repo.findByActivaTrue().stream().map(this::toDto).collect(Collectors.toList());
    }

    public SucursalDto create(SucursalDto dto) {
        Sucursal s = new Sucursal();
        s.setNombre(dto.getNombre());
        s.setDireccion(dto.getDireccion());
        s.setTelefono(dto.getTelefono());
        s.setPorcentajeComision(dto.getPorcentajeComision() != null ? dto.getPorcentajeComision() : 0.0);
        s.setActiva(dto.getActiva() != null ? dto.getActiva() : true);
        return toDto(repo.save(s));
    }

    public SucursalDto update(Long id, SucursalDto dto) {
        Sucursal s = repo.findById(id).orElseThrow(() -> new RuntimeException("Sucursal no encontrada"));
        if (dto.getNombre() != null) s.setNombre(dto.getNombre());
        if (dto.getDireccion() != null) s.setDireccion(dto.getDireccion());
        if (dto.getTelefono() != null) s.setTelefono(dto.getTelefono());
        if (dto.getPorcentajeComision() != null) s.setPorcentajeComision(dto.getPorcentajeComision());
        if (dto.getActiva() != null) s.setActiva(dto.getActiva());
        return toDto(repo.save(s));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    private SucursalDto toDto(Sucursal s) {
        return new SucursalDto(s.getId(), s.getNombre(), s.getDireccion(),
            s.getTelefono(), s.getPorcentajeComision(), s.getActiva());
    }
}
