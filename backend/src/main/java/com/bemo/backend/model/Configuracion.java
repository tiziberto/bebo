package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Configuración global del sistema (modelo clave-valor).
 * Tabla: "configuracion". PK es un String (clave), no un id numérico.
 */
@Data
@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    @Column(name = "clave", length = 60)
    private String clave;

    @Column(name = "valor", nullable = false, length = 500)
    private String valor = "";

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
