package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "recepcion")
public class Recepcion extends BaseAuditEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional prestador;

    @Column(name = "fecha_realiza")
    private LocalDate fechaRealiza;

    @Column(name = "coseguro", nullable = false, precision = 10, scale = 2)
    private BigDecimal coseguro = BigDecimal.ZERO;

    @Column(name = "pago_efectivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal pagoEfectivo = BigDecimal.ZERO;

    @Column(name = "tipo_pago_id", nullable = false)
    private Integer tipoPagoId = 0;
}
