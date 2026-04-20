package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoDto {
    private Long id;
    private Long pacienteId;
    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteDni;
    private String pacienteEmail;
    private String pacienteTelefono;
    private Long profesionalId;
    private String profesionalNombre;
    private String profesionalApellido;
    private Long sucursalId;
    private String sucursalNombre;
    private Long estudioId;
    private String estudioNombre;
    private Long obraSocialId;
    private String obraSocialNombre;
    private Long planId;
    private String planNombre;
    private String fechaHora;
    private String estado;
    private String observaciones;
    private String createdAt;
    private String confirmacionEnviadaAt;
}
