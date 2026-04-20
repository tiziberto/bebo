package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_social_id", nullable = false)
    private ObraSocial obraSocial;

    /** Mapeado a columna "descripcion" del schema SQL. */
    @Column(name = "descripcion", nullable = false, length = 50)
    private String nombre;

    @Transient
    private Boolean activo = true;
}
