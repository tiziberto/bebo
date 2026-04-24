package com.bemo.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ResultadoConfirmacionDto {
    private Long turnoId;
    private String paciente;              // "Apellido, Nombre"
    private String email;
    private boolean exitoso;
    private String razon;                 // Si falló: motivo
    private LocalDateTime intentoEnvio;
}
