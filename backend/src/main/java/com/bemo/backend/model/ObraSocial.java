package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "planes")
@Entity
@Table(name = "obra_social")
public class ObraSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mapeado a columna "descripcion" del schema SQL. */
    @Column(name = "descripcion", nullable = false, length = 150)
    private String nombre;

    /** Mapeado a columna "abreviatura" del schema SQL. */
    @Column(name = "abreviatura", length = 20)
    private String codigo;

    @Transient
    private String telefono;

    @Transient
    private String email;

    @Transient
    private Boolean activa = true;

    /** FK a condicion_iva según el schema SQL. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicion_iva_id")
    private CondicionIva condicionIva;

    // ── Auditoría ────────────────────────────────────────────────
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50, nullable = false, updatable = false)
    private String createdBy = "";

    @Column(name = "updated_by", length = 50, nullable = false)
    private String updatedBy = "";

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.createdBy == null) this.createdBy = "";
        if (this.updatedBy == null) this.updatedBy = "";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.updatedBy == null) this.updatedBy = "";
    }

    @OneToMany(mappedBy = "obraSocial", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Plan> planes = new ArrayList<>();
}
