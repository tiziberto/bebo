package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "orden_prestacion")
public class OrdenPrestacion extends BaseAuditEntity {

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
    @JoinColumn(name = "obra_social_id", nullable = false)
    private ObraSocial obraSocial;

    @Column(name = "nro_afiliado", length = 30, nullable = false)
    private String nroAfiliado = "";

    @Column(name = "nro_bono", nullable = false)
    private Integer nroBono = 0;

    @Column(name = "fecha_realiza")
    private LocalDate fechaRealiza;

    @Column(name = "codigo_nomencla", length = 10, nullable = false)
    private String codigoNomencla = "";

    @Column(name = "solicitante_id", nullable = false)
    private Integer solicitanteId = 0;

    @Column(name = "porc_solicitante", nullable = false)
    private Integer porcSolicitante = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional prestador;

    @Column(name = "porc_efector", nullable = false)
    private Integer porcEfector = 0;

    @Column(name = "importe", nullable = false, precision = 12, scale = 2)
    private BigDecimal importe = BigDecimal.ZERO;

    @Column(name = "honorario", nullable = false, precision = 10, scale = 2)
    private BigDecimal honorario = BigDecimal.ZERO;

    @Column(name = "gasto", nullable = false, precision = 10, scale = 2)
    private BigDecimal gasto = BigDecimal.ZERO;

    @Column(name = "iva", nullable = false, precision = 10, scale = 2)
    private BigDecimal iva = BigDecimal.ZERO;

    @Column(name = "importe_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal importeTotal = BigDecimal.ZERO;

    @Column(name = "coseguro", nullable = false, precision = 10, scale = 2)
    private BigDecimal coseguro = BigDecimal.ZERO;

    @Column(name = "pago_efectivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal pagoEfectivo = BigDecimal.ZERO;

    @Column(name = "tipo_pago_id", nullable = false)
    private Integer tipoPagoId = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_afiliado_id", nullable = false)
    private TipoAfiliado tipoAfiliado;

    @Column(name = "plan_id", nullable = false)
    private Integer planId = 0;

    @Column(name = "origen_id", nullable = false)
    private Integer origenId = 1;

    @Column(name = "periodo_id", nullable = false)
    private Integer periodoId = 0;

    @Column(name = "marcado", nullable = false)
    private Boolean marcado = false;

    @Column(name = "generado", nullable = false)
    private Boolean generado = false;

    @Column(name = "es_historico", nullable = false)
    private Boolean esHistorico = false;

    @Column(name = "marcado_por", length = 50, nullable = false)
    private String marcadoPor = "";

    @Column(name = "fecha_marca", nullable = false)
    private LocalDateTime fechaMarca;

    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (this.fechaMarca == null) {
            this.fechaMarca = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
        }
    }
}
