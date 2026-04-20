package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_id", nullable = false)
    private Integer tipoId = 0;

    @Column(name = "destinatario", nullable = false, length = 50)
    private String destinatario;

    @Column(name = "remitente", nullable = false, length = 50)
    private String remitente;

    @Column(name = "asunto", nullable = false, length = 250)
    private String asunto = "";

    @Column(name = "texto", columnDefinition = "TEXT", nullable = false)
    private String texto = "";

    @Column(name = "leido", nullable = false)
    private Boolean leido = false;

    @Column(name = "leido_en")
    private LocalDateTime leidoEn;

    @Column(name = "enviado_en", nullable = false)
    private LocalDateTime enviadoEn;

    @PrePersist
    protected void onCreate() {
        if (this.enviadoEn == null) this.enviadoEn = LocalDateTime.now();
    }
}
