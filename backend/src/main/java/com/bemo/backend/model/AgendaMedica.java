package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Horario de atención de un prestador.
 * Tabla: "horario" según el schema SQL.
 *
 * El schema SQL almacena todos los slots semanales en una sola fila por prestador.
 * Para compatibilidad con el código existente (que usa filas por día), se mantienen
 * los campos originales (sucursal, diaSemana, horaInicio, horaFin, duracionTurnoMinutos, activa)
 * como columnas adicionales en la misma tabla.
 */
@Data
@Entity
@Table(name = "horario")
public class AgendaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK al prestador. Campo "profesional" mapeado a columna "prestador_id"
     * del schema SQL. Los servicios y repositorios existentes siguen usando
     * getProfesional()/setProfesional().
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional profesional;

    /** No está en el schema SQL horario — @Transient. */
    @Transient
    private Sucursal sucursal;

    // ── Slots del schema SQL (uno por franja de cada día) ────────

    @Column(name = "lun_inicio_m") private LocalTime lunInicioM;
    @Column(name = "lun_fin_m")    private LocalTime lunFinM;
    @Column(name = "lun_inicio_t") private LocalTime lunInicioT;
    @Column(name = "lun_fin_t")    private LocalTime lunFinT;
    @Column(name = "lun_duracion") private LocalTime lunDuracion;

    @Column(name = "mar_inicio_m") private LocalTime marInicioM;
    @Column(name = "mar_fin_m")    private LocalTime marFinM;
    @Column(name = "mar_inicio_t") private LocalTime marInicioT;
    @Column(name = "mar_fin_t")    private LocalTime marFinT;
    @Column(name = "mar_duracion") private LocalTime marDuracion;

    @Column(name = "mie_inicio_m") private LocalTime mieInicioM;
    @Column(name = "mie_fin_m")    private LocalTime mieFinM;
    @Column(name = "mie_inicio_t") private LocalTime mieInicioT;
    @Column(name = "mie_fin_t")    private LocalTime mieFinT;
    @Column(name = "mie_duracion") private LocalTime mieDuracion;

    @Column(name = "jue_inicio_m") private LocalTime jueInicioM;
    @Column(name = "jue_fin_m")    private LocalTime jueFinM;
    @Column(name = "jue_inicio_t") private LocalTime jueInicioT;
    @Column(name = "jue_fin_t")    private LocalTime jueFinT;
    @Column(name = "jue_duracion") private LocalTime jueDuracion;

    @Column(name = "vie_inicio_m") private LocalTime vieInicioM;
    @Column(name = "vie_fin_m")    private LocalTime vieFinM;
    @Column(name = "vie_inicio_t") private LocalTime vieInicioT;
    @Column(name = "vie_fin_t")    private LocalTime vieFinT;
    @Column(name = "vie_duracion") private LocalTime vieDuracion;

    @Column(name = "sab_inicio_m") private LocalTime sabInicioM;
    @Column(name = "sab_fin_m")    private LocalTime sabFinM;
    @Column(name = "sab_inicio_t") private LocalTime sabInicioT;
    @Column(name = "sab_fin_t")    private LocalTime sabFinT;
    @Column(name = "sab_duracion") private LocalTime sabDuracion;

    // ── Campos extra — @Transient, no están en el schema SQL horario ──

    @Transient
    private Integer diaSemana;

    @Transient
    private LocalTime horaInicio;

    @Transient
    private LocalTime horaFin;

    @Transient
    private Integer duracionTurnoMinutos = 30;

    @Transient
    private Boolean activa = true;

    // ── Auditoría ────────────────────────────────────────────────
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50, updatable = false)
    private String createdBy = "";

    @Column(name = "updated_by", length = 50)
    private String updatedBy = "";

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
