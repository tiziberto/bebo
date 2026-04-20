package com.bemo.backend.service;

import com.bemo.backend.dto.AgendaMedicaDto;
import com.bemo.backend.model.AgendaMedica;
import com.bemo.backend.model.Profesional;
import com.bemo.backend.repository.AgendaMedicaRepository;
import com.bemo.backend.repository.ProfesionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaMedicaService {

    private final AgendaMedicaRepository repo;
    private final ProfesionalRepository profRepo;

    public List<AgendaMedicaDto> getAll() {
        return repo.findAll().stream()
            .flatMap(a -> expandSlots(a).stream())
            .collect(Collectors.toList());
    }

    public List<AgendaMedicaDto> getByProfesional(Long profesionalId) {
        return repo.findByProfesionalId(profesionalId).stream()
            .flatMap(a -> expandSlots(a).stream())
            .collect(Collectors.toList());
    }

    public List<AgendaMedicaDto> getByProfesionalAndSucursal(Long profesionalId, Long sucursalId) {
        return repo.findByProfesionalId(profesionalId).stream()
            .flatMap(a -> expandSlots(a).stream())
            .collect(Collectors.toList());
    }

    public AgendaMedicaDto create(AgendaMedicaDto dto) {
        Profesional prof = profRepo.findById(dto.getProfesionalId())
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        AgendaMedica a = new AgendaMedica();
        a.setProfesional(prof);
        a.setDiaSemana(dto.getDiaSemana());
        if (dto.getHoraInicio() != null) a.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        if (dto.getHoraFin() != null) a.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        a.setDuracionTurnoMinutos(dto.getDuracionTurnoMinutos() != null ? dto.getDuracionTurnoMinutos() : 30);
        a.setActiva(true);
        AgendaMedica saved = repo.save(a);
        List<AgendaMedicaDto> items = expandSlots(saved);
        return items.isEmpty() ? new AgendaMedicaDto(saved.getId(), saved.getProfesional().getId(),
                saved.getProfesional().getNombre(), null, null, dto.getDiaSemana(),
                dto.getHoraInicio(), dto.getHoraFin(), dto.getDuracionTurnoMinutos(), true) : items.get(0);
    }

    public AgendaMedicaDto update(Long id, AgendaMedicaDto dto) {
        AgendaMedica a = repo.findById(id).orElseThrow(() -> new RuntimeException("Agenda no encontrada"));
        if (dto.getDiaSemana() != null) a.setDiaSemana(dto.getDiaSemana());
        if (dto.getHoraInicio() != null) a.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
        if (dto.getHoraFin() != null) a.setHoraFin(LocalTime.parse(dto.getHoraFin()));
        if (dto.getDuracionTurnoMinutos() != null) a.setDuracionTurnoMinutos(dto.getDuracionTurnoMinutos());
        if (dto.getActiva() != null) a.setActiva(dto.getActiva());
        if (dto.getProfesionalId() != null) {
            a.setProfesional(profRepo.findById(dto.getProfesionalId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
        }
        AgendaMedica saved = repo.save(a);
        List<AgendaMedicaDto> items = expandSlots(saved);
        return items.isEmpty() ? new AgendaMedicaDto(saved.getId(), saved.getProfesional().getId(),
                saved.getProfesional().getNombre(), null, null, dto.getDiaSemana(),
                dto.getHoraInicio(), dto.getHoraFin(), dto.getDuracionTurnoMinutos(), true) : items.get(0);
    }

    public void delete(Long id) {
        repo.findById(id).orElseThrow(() -> new RuntimeException("Agenda no encontrada"));
        repo.deleteById(id);
    }

    /**
     * Expande una fila horario (una por prestador) en múltiples AgendaMedicaDto,
     * uno por franja horaria con hora de inicio distinta de medianoche.
     * Días: 1=Lun, 2=Mar, 3=Mié, 4=Jue, 5=Vie, 6=Sáb
     */
    private List<AgendaMedicaDto> expandSlots(AgendaMedica a) {
        Long profId = a.getProfesional().getId();
        String profNombre = a.getProfesional().getNombre();
        List<AgendaMedicaDto> items = new ArrayList<>();

        addSlot(items, a.getId(), profId, profNombre, 1, a.getLunInicioM(), a.getLunFinM(), a.getLunDuracion());
        addSlot(items, a.getId(), profId, profNombre, 1, a.getLunInicioT(), a.getLunFinT(), a.getLunDuracion());
        addSlot(items, a.getId(), profId, profNombre, 2, a.getMarInicioM(), a.getMarFinM(), a.getMarDuracion());
        addSlot(items, a.getId(), profId, profNombre, 2, a.getMarInicioT(), a.getMarFinT(), a.getMarDuracion());
        addSlot(items, a.getId(), profId, profNombre, 3, a.getMieInicioM(), a.getMieFinM(), a.getMieDuracion());
        addSlot(items, a.getId(), profId, profNombre, 3, a.getMieInicioT(), a.getMieFinT(), a.getMieDuracion());
        addSlot(items, a.getId(), profId, profNombre, 4, a.getJueInicioM(), a.getJueFinM(), a.getJueDuracion());
        addSlot(items, a.getId(), profId, profNombre, 4, a.getJueInicioT(), a.getJueFinT(), a.getJueDuracion());
        addSlot(items, a.getId(), profId, profNombre, 5, a.getVieInicioM(), a.getVieFinM(), a.getVieDuracion());
        addSlot(items, a.getId(), profId, profNombre, 5, a.getVieInicioT(), a.getVieFinT(), a.getVieDuracion());
        addSlot(items, a.getId(), profId, profNombre, 6, a.getSabInicioM(), a.getSabFinM(), a.getSabDuracion());
        addSlot(items, a.getId(), profId, profNombre, 6, a.getSabInicioT(), a.getSabFinT(), a.getSabDuracion());

        return items;
    }

    private void addSlot(List<AgendaMedicaDto> items, Long id, Long profId, String profNombre,
                         int dia, LocalTime inicio, LocalTime fin, LocalTime duracion) {
        if (inicio == null || inicio.equals(LocalTime.MIDNIGHT)) return;
        int minutos = duracion != null ? duracion.getMinute() + duracion.getHour() * 60 : 30;
        if (minutos == 0) minutos = 30;
        items.add(new AgendaMedicaDto(id, profId, profNombre, null, null,
                dia, inicio.toString(), fin != null ? fin.toString() : null, minutos, true));
    }
}
