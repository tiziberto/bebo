package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "especialidad",
       uniqueConstraints = @UniqueConstraint(name = "uq_especialidad_descripcion", columnNames = "descripcion"))
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Campo Java "nombre" mapeado a columna "descripcion" (nombre del schema SQL).
     * Los servicios existentes siguen usando getNombre()/setNombre().
     */
    @Column(name = "descripcion", nullable = false, length = 100)
    private String nombre;

    /** No existe en el schema SQL — solo en memoria para compatibilidad. */
    @Transient
    private String descripcion;

    @Transient
    private Boolean activa = true;
}
