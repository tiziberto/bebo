package com.bemo.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RespuestaConfirmacionesMasivasDto {
    private int total;                    // Total de turnos procesados
    private int enviados;                 // Confirmaciones exitosas
    private int fallidos;                 // Confirmaciones fallidas
    private LocalDate fecha;
    private String profesional;           // Nombre del profesional (si filtrado)
    private LocalDateTime procesadoEn;
    private List<ResultadoConfirmacionDto> detalles;
}
