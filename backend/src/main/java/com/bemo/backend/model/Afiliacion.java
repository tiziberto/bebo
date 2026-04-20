package com.bemo.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "afiliacion",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_afiliacion_paciente_os",
           columnNames = {"paciente_id", "obra_social_id"}))
public class Afiliacion extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obra_social_id", nullable = false)
    private ObraSocial obraSocial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "nro_afiliado", length = 25, nullable = false)
    private String nroAfiliado = "";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_afiliado_id", nullable = false)
    private TipoAfiliado tipoAfiliado;
}
