package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "caja")
public class Caja extends BaseAuditEntity {

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

    @Column(name = "obra_social_id", nullable = false)
    private Integer obraSocialId = 0;

    @Column(name = "nro_bono", nullable = false)
    private Integer nroBono = 0;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional prestador;

    @Column(name = "coseguro", nullable = false, precision = 10, scale = 2)
    private BigDecimal coseguro = BigDecimal.ZERO;

    @Column(name = "pago_efectivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal pagoEfectivo = BigDecimal.ZERO;

    @Column(name = "en_deposito", nullable = false)
    private Boolean enDeposito = false;

    @Column(name = "observaciones", columnDefinition = "TEXT", nullable = false)
    private String observaciones = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;
}
