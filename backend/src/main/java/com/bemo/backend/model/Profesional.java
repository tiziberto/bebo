package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"obrasSociales", "estudios", "sucursales", "especialidades", "user"})
@Entity
@Table(name = "prestador")
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Transient
    private String apellido;

    @Column(length = 50)
    private String dni;

    @Column(length = 50)
    private String matricula;

    @Column(length = 50)
    private String telefono;

    @Column(length = 150)
    private String email;

    /** Categoría del prestador (ej: 'A', 'B'). */
    @Column(name = "categoria", length = 1)
    private String categoria = "A";

    @Column(name = "cuit", length = 20)
    private String cuit = "S/D";

    /** FK a condicion_iva según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicion_iva_id")
    private CondicionIva condicionIva;

    /** Porcentaje del efector (médico). */
    @Column(name = "porcentaje_efe")
    private Integer porcentajeEfe = 0;

    /**
     * Especialidad como texto libre según el schema SQL.
     * La columna se llama "especialidad" (VARCHAR 150).
     * El código existente usa la relación ManyToMany "especialidades" que crea
     * una tabla join separada; esto es una columna adicional.
     */
    @Column(name = "especialidad", length = 150)
    private String especialidadTexto = "Sin especificar";

    @Column(name = "asigna_turnos", nullable = false)
    private Boolean asignaTurnos = false;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    // ── Relaciones ManyToMany mantenidas para compatibilidad ──────
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profesional_especialidades",
        joinColumns = @JoinColumn(name = "profesional_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id"))
    private Set<Especialidad> especialidades = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profesional_obras_sociales",
        joinColumns = @JoinColumn(name = "profesional_id"),
        inverseJoinColumns = @JoinColumn(name = "obra_social_id"))
    private Set<ObraSocial> obrasSociales = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profesional_estudios",
        joinColumns = @JoinColumn(name = "profesional_id"),
        inverseJoinColumns = @JoinColumn(name = "estudio_id"))
    private Set<Estudio> estudios = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "profesional_sucursales",
        joinColumns = @JoinColumn(name = "profesional_id"),
        inverseJoinColumns = @JoinColumn(name = "sucursal_id"))
    private Set<Sucursal> sucursales = new HashSet<>();

    /** No está en prestador del schema SQL — @Transient. */
    @Transient
    private User user;

    @Transient
    private Boolean activo = true;

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
        // Ensure cuit has a default value (database requires it to be NOT NULL)
        if (this.cuit == null) this.cuit = "S/D";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.updatedBy == null) this.updatedBy = "";
    }
}
