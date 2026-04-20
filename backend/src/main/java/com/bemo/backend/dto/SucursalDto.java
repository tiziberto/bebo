package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucursalDto {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private Double porcentajeComision;
    private Boolean activa;
}
