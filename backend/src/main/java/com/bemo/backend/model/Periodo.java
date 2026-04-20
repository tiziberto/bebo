package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "periodo")
public class Periodo {

    /**
     * Ej: 202306 = junio 2023
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String descripcion;

    @Column(nullable = false)
    private Boolean cerrado = false;
}
