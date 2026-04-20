package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "url_corta",
       uniqueConstraints = @UniqueConstraint(name = "uq_url_corta_codigo", columnNames = "codigo"))
public class UrlCorta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(name = "url_destino", nullable = false, length = 1000)
    private String urlDestino;

    @Column(nullable = false)
    private Long clics = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }
}
