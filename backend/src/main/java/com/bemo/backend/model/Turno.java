package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "turno")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Identificador de consultorio (TINYINT en el schema SQL, sin tabla FK). */
    @Column(name = "consultorio_id")
    private Integer consultorioId = 1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    /**
     * FK al prestador. Campo Java "profesional" mapeado a columna "prestador_id"
     * del schema SQL. Los repositorios y servicios existentes siguen usando
     * getProfesional()/setProfesional().
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional profesional;

    /** No está en el schema SQL turno — @Transient. */
    @Transient
    private Sucursal sucursal;

    @Transient
    private Estudio estudio;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "obra_social_id")
    private ObraSocial obraSocial;

    @Transient
    private Plan plan;

    /** Fecha del turno (DATE en el schema SQL). */
    @Column(name = "fecha")
    private LocalDate fecha;

    /** Hora del turno (TIME en el schema SQL). */
    @Column(name = "hora")
    private LocalTime hora;

    /** No está en el schema SQL — @Transient. Usar fecha + hora. */
    @Transient
    private LocalDateTime fechaHora;

    /** No está en el schema SQL — @Transient. Usar estado_id FK. */
    @Transient
    private String estado = "PENDIENTE";

    /** FK al estado del turno según el schema SQL (estado_id). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoTurno estadoTurno;

    @Column(name = "observaciones", length = 200)
    private String observaciones;

    @Column(name = "fijo", nullable = false)
    private Boolean fijo = false;

    // ── Auditoría ────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy = "";

    @Column(name = "updated_by", length = 50)
    private String updatedBy = "";

    @Transient
    private LocalDateTime confirmacionEnviadaAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) this.createdAt = now;
        this.updatedAt = now;
        if (this.createdBy == null) this.createdBy = "";
        if (this.updatedBy == null) this.updatedBy = "";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.updatedBy == null) this.updatedBy = "";
    }
}
