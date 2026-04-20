package com.bemo.backend.repository;

import com.bemo.backend.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Long> {

    @Query("SELECT t FROM Turno t WHERE t.fecha = :fecha ORDER BY t.hora ASC")
    List<Turno> findByFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profId AND t.fecha = :fecha ORDER BY t.hora ASC")
    List<Turno> findByProfesionalAndFecha(@Param("profId") Long profesionalId, @Param("fecha") LocalDate fecha);

    @Query("SELECT t FROM Turno t WHERE t.paciente.id = :pacId ORDER BY t.fecha DESC, t.hora DESC")
    List<Turno> findByPacienteId(@Param("pacId") Long pacienteId);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profId AND t.fecha = :fecha AND t.hora = :hora")
    List<Turno> findConflictos(@Param("profId") Long profesionalId, @Param("fecha") LocalDate fecha, @Param("hora") LocalTime hora);

    @Query("SELECT t FROM Turno t WHERE t.fecha >= :desde AND t.fecha <= :hasta ORDER BY t.fecha ASC, t.hora ASC")
    List<Turno> findByRango(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT t FROM Turno t WHERE t.profesional.id = :profId AND t.fecha >= :desde AND t.fecha <= :hasta ORDER BY t.fecha ASC, t.hora ASC")
    List<Turno> findByProfesionalAndRango(@Param("profId") Long profesionalId, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
