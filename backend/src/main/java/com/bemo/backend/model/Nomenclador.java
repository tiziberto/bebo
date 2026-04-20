package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "nomenclador",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_nomenclador_periodo_os_codigo",
           columnNames = {"periodo_id", "obra_social_id", "codigo"}))
public class Nomenclador extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "periodo_id", nullable = false)
    private Integer periodoId = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_social_id", nullable = false)
    private ObraSocial obraSocial;

    @Column(nullable = false, length = 10)
    private String codigo = "";

    @Column(nullable = false, length = 200)
    private String descripcion = "";

    @Column(name = "id_galeno", nullable = false)
    private Integer idGaleno = 0;

    @Column(name = "valor_galeno", nullable = false, precision = 12, scale = 4)
    private BigDecimal valorGaleno = BigDecimal.ZERO;

    @Column(name = "id_gasto", nullable = false)
    private Integer idGasto = 0;

    @Column(name = "unidades_gasto", nullable = false, precision = 12, scale = 4)
    private BigDecimal unidadesGasto = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @Column(nullable = false)
    private Boolean vigente = true;

    @Column(name = "importe_tit_a", nullable = false, precision = 12, scale = 4)
    private BigDecimal importeTitA = BigDecimal.ZERO;

    @Column(name = "importe_gas_a", nullable = false, precision = 12, scale = 4)
    private BigDecimal importeGasA = BigDecimal.ZERO;

    @Column(name = "importe_tit_b", nullable = false, precision = 12, scale = 4)
    private BigDecimal importeTitB = BigDecimal.ZERO;

    @Column(name = "importe_gas_b", nullable = false, precision = 12, scale = 4)
    private BigDecimal importeGasB = BigDecimal.ZERO;

    @Column(name = "importe_tit_c", nullable = false, precision = 12, scale = 4)
    private BigDecimal importeTitC = BigDecimal.ZERO;

    @Column(name = "importe_gas_c", nullable = false, precision = 12, scale = 4)
    private BigDecimal importeGasC = BigDecimal.ZERO;
}
