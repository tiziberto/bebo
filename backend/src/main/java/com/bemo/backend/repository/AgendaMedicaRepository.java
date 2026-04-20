package com.bemo.backend.repository;

import com.bemo.backend.model.AgendaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendaMedicaRepository extends JpaRepository<AgendaMedica, Long> {
    List<AgendaMedica> findByProfesionalId(Long profesionalId);
}
