package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "informe_estudio")
public class InformeEstudio extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nro_asignado", nullable = false)
    private Long nroAsignado = 0L;

    @Column(name = "nro_informe", nullable = false)
    private Long nroInforme = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Profesional prestador;

    @Column(name = "codigo_nomencla", length = 20, nullable = false)
    private String codigoNomencla = "";

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto = "";

    @Column(name = "fecha_realiza", nullable = false)
    private LocalDate fechaRealiza;

    @Column(name = "fecha_informe", nullable = false)
    private LocalDate fechaInforme;

    @Column(name = "enviado", nullable = false)
    private Boolean enviado = false;
}
