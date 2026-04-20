package com.bemo.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObraSocialDto {
    private Long id;
    private String nombre;
    private String codigo;
    private String telefono;
    private String email;
    private Boolean activa;
    private List<PlanDto> planes;
}
