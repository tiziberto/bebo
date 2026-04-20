package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "deposito")
public class Deposito extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consultorio_id", nullable = false)
    private Integer consultorioId = 1;

    @Column(name = "nro_asignado", nullable = false)
    private Long nroAsignado = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional prestador;

    @Column(name = "importe", nullable = false, precision = 12, scale = 2)
    private BigDecimal importe = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_recupero_id", nullable = false)
    private TipoRecupero tipoRecupero;

    @Column(name = "fecha_recupero", nullable = false)
    private LocalDateTime fechaRecupero;

    @Column(name = "recuperado_por", length = 50, nullable = false)
    private String recuperadoPor = "";

    @Column(name = "nro_recupero", nullable = false)
    private Long nroRecupero = 0L;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.fechaRecupero == null) {
            this.fechaRecupero = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        }
    }
}
