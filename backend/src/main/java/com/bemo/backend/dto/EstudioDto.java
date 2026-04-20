package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudioDto {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private Boolean activo;
}
