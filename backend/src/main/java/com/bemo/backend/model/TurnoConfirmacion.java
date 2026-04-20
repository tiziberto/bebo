package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "turno_confirmacion")
public class TurnoConfirmacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional prestador;

    @Column(name = "fecha_turno", nullable = false)
    private LocalDate fechaTurno;

    @Column(name = "hora_turno", nullable = false)
    private LocalTime horaTurno;

    @Column(name = "observaciones", length = 200, nullable = false)
    private String observaciones = "";

    @Column(name = "visto_en", nullable = false)
    private LocalDateTime vistoEn;

    @PrePersist
    protected void onCreate() {
        if (this.vistoEn == null) this.vistoEn = LocalDateTime.now();
    }
}
