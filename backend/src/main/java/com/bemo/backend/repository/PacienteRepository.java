package com.bemo.backend.repository;

import com.bemo.backend.model.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByDocumento(BigInteger documento);
    boolean existsByDocumento(BigInteger documento);

    @Query("SELECT p FROM Paciente p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "CAST(p.documento AS string) LIKE CONCAT('%', :q, '%')")
    Page<Paciente> searchPage(@Param("q") String query, Pageable pageable);
}
