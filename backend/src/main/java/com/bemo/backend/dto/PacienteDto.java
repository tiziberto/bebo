package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String fechaNacimiento;
    private String sexo;
    private String telefono;
    private String email;
    private String direccion;
    private Long obraSocialId;
    private String obraSocialNombre;
    private Long planId;
    private String planNombre;
    private String numeroAfiliado;
    private String observaciones;
    private Boolean activo;
}
