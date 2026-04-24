package com.bemo.backend.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EnviarConfirmacionesFechaRequest {
    private LocalDate fecha;              // Requerido
    private Long profesionalId;           // Opcional
    private String estado;                // Opcional: filtrar por estado
    private boolean incluirConfirmados;   // default: false (solo pendientes)
}
