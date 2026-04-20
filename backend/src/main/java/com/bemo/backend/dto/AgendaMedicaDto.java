package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgendaMedicaDto {
    private Long id;
    private Long profesionalId;
    private String profesionalNombre;
    private Long sucursalId;
    private String sucursalNombre;
    private Integer diaSemana;
    private String horaInicio;
    private String horaFin;
    private Integer duracionTurnoMinutos;
    private Boolean activa;
}
