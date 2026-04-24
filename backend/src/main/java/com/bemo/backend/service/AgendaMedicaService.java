package com.bemo.backend.service;

import com.bemo.backend.dto.AgendaMedicaDto;
import com.bemo.backend.model.AgendaMedica;
import com.bemo.backend.model.Profesional;
import com.bemo.backend.model.Turno;
import com.bemo.backend.repository.AgendaMedicaRepository;
import com.bemo.backend.repository.ProfesionalRepository;
import com.bemo.backend.repository.TurnoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendaMedicaService {

    private final AgendaMedicaRepository repo;
    private final ProfesionalRepository profRepo;
    private final TurnoRepository turnoRepo;

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

    /**
     * Retorna los horarios disponibles para un profesional entre dos fechas.
     * Calcula los slots basados en la agenda y excluye los turnos ya asignados.
     */
    public List<Map<String, Object>> getDisponibles(Long profesionalId, Long sucursalId, LocalDate desde, LocalDate hasta) {
        AgendaMedica agenda = repo.findByProfesionalId(profesionalId).stream().findFirst()
            .orElseThrow(() -> new RuntimeException("No hay agenda definida para este profesional"));

        List<Turno> turnosOcupados = turnoRepo.findByProfesionalAndRango(profesionalId, desde, hasta);
        Map<String, LocalTime> ocupados = new HashMap<>();
        for (Turno t : turnosOcupados) {
            String key = t.getFecha().toString() + "T" + String.format("%02d:%02d", t.getHora().getHour(), t.getHora().getMinute());
            ocupados.put(key, t.getHora());
        }

        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate current = desde;
        while (!current.isAfter(hasta)) {
            int dayOfWeek = current.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
            if (dayOfWeek > 6) dayOfWeek = 7; // Convertir Sunday de 7 a 7

            List<Map<String, String>> slots = new ArrayList<>();

            // Obtener horarios para este día de la semana
            LocalTime inicioM = getInicioForDay(agenda, dayOfWeek, true);
            LocalTime finM = getFinForDay(agenda, dayOfWeek, true);
            LocalTime inicioT = getInicioForDay(agenda, dayOfWeek, false);
            LocalTime finT = getFinForDay(agenda, dayOfWeek, false);
            LocalTime duracion = getDuracionForDay(agenda, dayOfWeek);

            int minutos = duracion != null ? duracion.getMinute() + duracion.getHour() * 60 : 30;
            if (minutos == 0) minutos = 30;

            // Agregar slots de la mañana
            if (inicioM != null && finM != null) {
                addSlotsForRange(slots, current, inicioM, finM, minutos, ocupados);
            }

            // Agregar slots de la tarde
            if (inicioT != null && finT != null) {
                addSlotsForRange(slots, current, inicioT, finT, minutos, ocupados);
            }

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", current.toString());
            dayData.put("slots", slots);
            result.add(dayData);

            current = current.plusDays(1);
        }

        return result;
    }

    private void addSlotsForRange(List<Map<String, String>> slots, LocalDate date, LocalTime inicio, LocalTime fin,
                                   int minutos, Map<String, LocalTime> ocupados) {
        LocalTime current = inicio;
        while (current.isBefore(fin)) {
            LocalDateTime dateTime = LocalDateTime.of(date, current);
            String key = dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            boolean available = !ocupados.containsKey(key);

            Map<String, String> slot = new HashMap<>();
            slot.put("time", current.toString());
            slot.put("available", String.valueOf(available));
            slots.add(slot);

            current = current.plusMinutes(minutos);
        }
    }

    private LocalTime getInicioForDay(AgendaMedica a, int dayOfWeek, boolean mañana) {
        return switch (dayOfWeek) {
            case 1 -> mañana ? a.getLunInicioM() : a.getLunInicioT();
            case 2 -> mañana ? a.getMarInicioM() : a.getMarInicioT();
            case 3 -> mañana ? a.getMieInicioM() : a.getMieInicioT();
            case 4 -> mañana ? a.getJueInicioM() : a.getJueInicioT();
            case 5 -> mañana ? a.getVieInicioM() : a.getVieInicioT();
            case 6 -> mañana ? a.getSabInicioM() : a.getSabInicioT();
            default -> null;
        };
    }

    private LocalTime getFinForDay(AgendaMedica a, int dayOfWeek, boolean mañana) {
        return switch (dayOfWeek) {
            case 1 -> mañana ? a.getLunFinM() : a.getLunFinT();
            case 2 -> mañana ? a.getMarFinM() : a.getMarFinT();
            case 3 -> mañana ? a.getMieFinM() : a.getMieFinT();
            case 4 -> mañana ? a.getJueFinM() : a.getJueFinT();
            case 5 -> mañana ? a.getVieFinM() : a.getVieFinT();
            case 6 -> mañana ? a.getSabFinM() : a.getSabFinT();
            default -> null;
        };
    }

    private LocalTime getDuracionForDay(AgendaMedica a, int dayOfWeek) {
        return switch (dayOfWeek) {
            case 1 -> a.getLunDuracion();
            case 2 -> a.getMarDuracion();
            case 3 -> a.getMieDuracion();
            case 4 -> a.getJueDuracion();
            case 5 -> a.getVieDuracion();
            case 6 -> a.getSabDuracion();
            default -> null;
        };
    }
}
