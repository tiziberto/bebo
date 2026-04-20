package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionalDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String matricula;
    private String telefono;
    private String email;
    private Set<Long> especialidadesIds;
    private Set<Long> obrasSocialesIds;
    private Set<Long> estudiosIds;
    private Set<Long> sucursalesIds;
    private Long userId;
    private Boolean activo;
}
