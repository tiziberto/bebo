package com.bemo.backend.repository;

import com.bemo.backend.model.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {

    List<Profesional> findByFechaBajaIsNull();

    @Query("SELECT p FROM Profesional p WHERE p.fechaBaja IS NULL AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "p.matricula LIKE CONCAT('%', :q, '%'))")
    List<Profesional> search(@Param("q") String query);

    @Query("SELECT p FROM Profesional p JOIN p.sucursales s WHERE s.id = :sucursalId AND p.fechaBaja IS NULL")
    List<Profesional> findBySucursalId(@Param("sucursalId") Long sucursalId);

    Optional<Profesional> findById(Long id);

    Optional<Profesional> findByDni(String dni);
}
