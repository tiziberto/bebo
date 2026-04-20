package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre completo del paciente (VARCHAR 150 en el schema SQL). */
    @Column(nullable = false, length = 150)
    private String nombre;

    @Transient
    private String apellido;

    /** DNI como texto — @Transient. En el schema SQL el DNI es "documento" BIGINT. */
    @Transient
    private String dni;

    /** Número de documento (BIGINT UNSIGNED en el schema SQL). Usamos BigInteger para soportar valores > Long.MAX_VALUE. */
    @Column(name = "documento")
    private BigInteger documento;

    /** Mapeado a columna "fecha_nac" del schema SQL. */
    @Column(name = "fecha_nac")
    private LocalDate fechaNacimiento;

    /** Sexo como texto libre — @Transient. El schema SQL usa sexo_id FK. */
    @Transient
    private String sexo;

    /** FK a tabla sexo según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sexo_id")
    private Sexo sexoEntidad;

    /** Mapeado a columna "telefono1" del schema SQL. */
    @Column(name = "telefono1", length = 50)
    private String telefono;

    @Column(name = "telefono2", length = 50)
    private String telefono2;

    @Column(length = 150)
    private String email;

    /** Mapeado a columna "domicilio" del schema SQL. */
    @Column(name = "domicilio", length = 100)
    private String direccion;

    /** FK a localidad según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    /** No está en paciente del schema SQL — la relación está en tabla afiliacion. */
    @Transient
    private ObraSocial obraSocial;

    @Transient
    private Plan plan;

    @Transient
    private String numeroAfiliado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Transient
    private Boolean activo = true;

    /** Médico de cabecera según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_cab_id")
    private Profesional medicoCabecera;

    @Column(name = "nro_interno", length = 10)
    private String nroInterno;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

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
