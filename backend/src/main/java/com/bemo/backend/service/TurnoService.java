package com.bemo.backend.service;

import com.bemo.backend.dto.TurnoDto;
import com.bemo.backend.model.*;
import com.bemo.backend.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository repo;
    private final PacienteRepository pacienteRepo;
    private final ProfesionalRepository profRepo;
    private final SucursalRepository sucursalRepo;
    private final EstudioRepository estudioRepo;
    private final ObraSocialRepository osRepo;
    private final PlanRepository planRepo;
    private final EstadoTurnoRepository estadoTurnoRepo;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<TurnoDto> getByFecha(String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return repo.findByFecha(date).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TurnoDto> getByProfesionalAndFecha(Long profesionalId, String fecha) {
        LocalDate date = LocalDate.parse(fecha);
        return repo.findByProfesionalAndFecha(profesionalId, date)
            .stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TurnoDto> getBySucursalAndFecha(Long sucursalId, String fecha) {
        // sucursal no está en la tabla turno del schema SQL — filtra solo por fecha
        LocalDate date = LocalDate.parse(fecha);
        return repo.findByFecha(date).stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<TurnoDto> getByRango(String desde, String hasta, Long profesionalId) {
        LocalDate desdeDate = parseFlexible(desde).toLocalDate();
        LocalDate hastaDate = parseFlexible(hasta).toLocalDate();
        if (profesionalId != null) {
            return repo.findByProfesionalAndRango(profesionalId, desdeDate, hastaDate)
                .stream().map(this::toDto).collect(Collectors.toList());
        }
        return repo.findByRango(desdeDate, hastaDate).stream().map(this::toDto).collect(Collectors.toList());
    }

    private LocalDateTime parseFlexible(String s) {
        if (s != null && s.contains("T")) {
            return LocalDateTime.parse(s, DT_FMT);
        }
        return LocalDate.parse(s).atStartOfDay();
    }

    public List<TurnoDto> getByPaciente(Long pacienteId) {
        return repo.findByPacienteId(pacienteId).stream().map(this::toDto).collect(Collectors.toList());
    }

    public TurnoDto create(TurnoDto dto) {
        Paciente paciente = pacienteRepo.findById(dto.getPacienteId())
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        Profesional prof = profRepo.findById(dto.getProfesionalId())
            .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        LocalDateTime fechaHora = LocalDateTime.parse(dto.getFechaHora(), DT_FMT);
        LocalDate fecha = fechaHora.toLocalDate();
        LocalTime hora = fechaHora.toLocalTime();

        List<Turno> conflictos = repo.findConflictos(prof.getId(), fecha, hora);
        if (!conflictos.isEmpty()) {
            throw new RuntimeException("El profesional ya tiene un turno en ese horario");
        }

        Turno t = new Turno();
        t.setPaciente(paciente);
        t.setProfesional(prof);
        t.setFecha(fecha);
        t.setHora(hora);
        t.setFechaHora(fechaHora);
        t.setEstadoTurno(estadoTurnoRepo.findById(2).orElse(null)); // 2 = Asignado
        t.setObservaciones(dto.getObservaciones());

        if (dto.getSucursalId() != null) t.setSucursal(sucursalRepo.findById(dto.getSucursalId()).orElse(null));
        if (dto.getEstudioId() != null) t.setEstudio(estudioRepo.findById(dto.getEstudioId()).orElse(null));
        if (dto.getObraSocialId() != null) t.setObraSocial(osRepo.findById(dto.getObraSocialId()).orElse(null));
        if (dto.getPlanId() != null) t.setPlan(planRepo.findById(dto.getPlanId()).orElse(null));

        return toDto(repo.save(t));
    }

    public TurnoDto updateEstado(Long id, String nuevoEstado) {
        Turno t = repo.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        t.setEstadoTurno(estadoTurnoRepo.findById(estadoStringToId(nuevoEstado)).orElse(null));
        t.setUpdatedAt(LocalDateTime.now());
        return toDto(repo.save(t));
    }

    /** Mapea el string del frontend al id en tabla estado_turno. */
    private int estadoStringToId(String estado) {
        return switch (estado) {
            case "CONFIRMADO"  -> 5;
            case "EN_ESPERA"   -> 4;
            case "EN_CURSO"    -> 4;
            case "ATENDIDO"    -> 7;
            case "CANCELADO"   -> 3;
            case "AUSENTE"     -> 3;
            default            -> 2; // PENDIENTE → Asignado
        };
    }

    public TurnoDto reprogramar(Long id, String nuevaFechaHora) {
        Turno t = repo.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        LocalDateTime newDt = LocalDateTime.parse(nuevaFechaHora, DT_FMT);
        LocalDate newFecha = newDt.toLocalDate();
        LocalTime newHora = newDt.toLocalTime();

        List<Turno> conflictos = repo.findConflictos(t.getProfesional().getId(), newFecha, newHora);
        conflictos.removeIf(c -> c.getId().equals(id));
        if (!conflictos.isEmpty()) throw new RuntimeException("El profesional ya tiene un turno en ese horario");

        t.setFecha(newFecha);
        t.setHora(newHora);
        t.setFechaHora(newDt);
        t.setEstado("PENDIENTE");
        t.setUpdatedAt(LocalDateTime.now());
        return toDto(repo.save(t));
    }

    public TurnoDto cancelar(Long id) {
        return updateEstado(id, "CANCELADO");
    }

    public TurnoDto update(Long id, TurnoDto dto) {
        Turno t = repo.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        if (dto.getPacienteId() != null) t.setPaciente(pacienteRepo.findById(dto.getPacienteId()).orElseThrow());
        if (dto.getProfesionalId() != null) t.setProfesional(profRepo.findById(dto.getProfesionalId()).orElseThrow());
        if (dto.getSucursalId() != null) t.setSucursal(sucursalRepo.findById(dto.getSucursalId()).orElse(null));
        if (dto.getFechaHora() != null) {
            LocalDateTime dt = LocalDateTime.parse(dto.getFechaHora(), DT_FMT);
            t.setFecha(dt.toLocalDate());
            t.setHora(dt.toLocalTime());
            t.setFechaHora(dt);
        }
        if (dto.getEstudioId() != null) t.setEstudio(estudioRepo.findById(dto.getEstudioId()).orElse(null));
        if (dto.getObraSocialId() != null) t.setObraSocial(osRepo.findById(dto.getObraSocialId()).orElse(null));
        if (dto.getPlanId() != null) t.setPlan(planRepo.findById(dto.getPlanId()).orElse(null));
        if (dto.getObservaciones() != null) t.setObservaciones(dto.getObservaciones());
        if (dto.getEstado() != null) t.setEstado(dto.getEstado());
        t.setUpdatedAt(LocalDateTime.now());
        return toDto(repo.save(t));
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public TurnoDto toDto(Turno t) {
        String fechaHoraStr = null;
        if (t.getFecha() != null && t.getHora() != null) {
            fechaHoraStr = t.getFecha().atTime(t.getHora()).toString();
        } else if (t.getFecha() != null) {
            fechaHoraStr = t.getFecha().toString();
        } else if (t.getFechaHora() != null) {
            fechaHoraStr = t.getFechaHora().toString();
        }

        Paciente pac = t.getPaciente();
        String pacDni = pac != null && pac.getDocumento() != null ? pac.getDocumento().toString() : null;

        return new TurnoDto(
            t.getId(),
            pac != null ? pac.getId() : null,
            pac != null ? pac.getNombre() : null,
            null,
            pacDni,
            pac != null ? pac.getEmail() : null,
            pac != null ? pac.getTelefono() : null,
            t.getProfesional().getId(),
            t.getProfesional().getNombre(),
            null,
            null, null,
            null, null,
            t.getObraSocial() != null ? t.getObraSocial().getId() : null,
            t.getObraSocial() != null ? t.getObraSocial().getNombre() : null,
            null, null,
            fechaHoraStr,
            estadoIdToString(t.getEstadoTurno()),
            t.getObservaciones(),
            t.getCreatedAt() != null ? t.getCreatedAt().toString() : null,
            null
        );
    }

    /** Mapea el id de estado_turno al string que usa el frontend. */
    private String estadoIdToString(EstadoTurno et) {
        if (et == null) return "PENDIENTE";
        return switch (et.getId()) {
            case 1 -> "DISPONIBLE";
            case 2 -> "PENDIENTE";
            case 3 -> "CANCELADO";
            case 4 -> "EN_ESPERA";
            case 5 -> "CONFIRMADO";
            case 6 -> "PENDIENTE";
            case 7 -> "ATENDIDO";
            default -> "PENDIENTE";
        };
    }
}
