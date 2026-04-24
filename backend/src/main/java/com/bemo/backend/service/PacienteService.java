package com.bemo.backend.service;

import com.bemo.backend.dto.PageResponse;
import com.bemo.backend.dto.PacienteDto;
import com.bemo.backend.model.Paciente;
import com.bemo.backend.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repo;

    public PageResponse<PacienteDto> getPage(String query, int page, int size) {
        PageRequest pr = PageRequest.of(page, size, Sort.by("nombre"));
        if (query == null || query.isBlank()) {
            return PageResponse.of(repo.findAll(pr).map(this::toDto));
        }
        return PageResponse.of(repo.searchPage(query.trim(), pr).map(this::toDto));
    }

    /** Used internally (e.g. dropdown lookups — returns all without pagination). */
    public List<PacienteDto> getAll() {
        return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public PacienteDto getById(Long id) {
        return toDto(repo.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado")));
    }

    public PacienteDto getByDni(String dni) {
        try {
            BigInteger documento = new BigInteger(dni.trim());
            return toDto(repo.findByDocumento(documento)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado")));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Paciente no encontrado");
        }
    }

    public PacienteDto create(PacienteDto dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }
        if (dto.getDni() == null || dto.getDni().isBlank()) {
            throw new RuntimeException("El DNI es obligatorio.");
        }
        if (dto.getFechaNacimiento() == null || dto.getFechaNacimiento().isBlank()) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria.");
        }
        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            throw new RuntimeException("El teléfono es obligatorio.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio.");
        }
        if (dto.getDni() != null) {
            try {
                BigInteger documento = new BigInteger(dto.getDni().trim());
                if (repo.existsByDocumento(documento)) {
                    throw new RuntimeException("Ya existe un paciente con DNI " + dto.getDni());
                }
            } catch (NumberFormatException ignored) {}
        }
        Paciente p = new Paciente();
        mapFromDto(p, dto);
        p.setFechaAlta(LocalDate.now());
        return toDto(repo.save(p));
    }

    public PacienteDto update(Long id, PacienteDto dto) {
        if (dto.getFechaNacimiento() == null || dto.getFechaNacimiento().isBlank()) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria.");
        }
        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            throw new RuntimeException("El teléfono es obligatorio.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio.");
        }
        Paciente p = repo.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        mapFromDto(p, dto);
        return toDto(repo.save(p));
    }

    public void delete(Long id) {
        repo.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        repo.deleteById(id);
    }

    private void mapFromDto(Paciente p, PacienteDto dto) {
        if (dto.getNombre() != null) p.setNombre(dto.getNombre());
        if (dto.getDni() != null) {
            try {
                p.setDocumento(new BigInteger(dto.getDni().trim()));
            } catch (NumberFormatException ignored) {}
        }
        if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isBlank()) {
            p.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        }
        if (dto.getTelefono() != null) p.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) p.setEmail(dto.getEmail());
        if (dto.getDireccion() != null) p.setDireccion(dto.getDireccion());
        if (dto.getObservaciones() != null) p.setObservaciones(dto.getObservaciones());
    }

    private PacienteDto toDto(Paciente p) {
        return new PacienteDto(
            p.getId(),
            p.getNombre(),
            null,
            p.getDocumento() != null ? p.getDocumento().toString() : null,
            p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : null,
            null,
            p.getTelefono(),
            p.getEmail(),
            p.getDireccion(),
            null, null, null, null,
            null,
            p.getObservaciones(),
            true
        );
    }
}
