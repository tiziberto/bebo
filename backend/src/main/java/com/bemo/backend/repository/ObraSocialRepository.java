package com.bemo.backend.repository;

import com.bemo.backend.model.ObraSocial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObraSocialRepository extends JpaRepository<ObraSocial, Long> {
    boolean existsByNombre(String nombre);

    @Query("SELECT o FROM ObraSocial o WHERE LOWER(o.nombre) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<ObraSocial> searchPage(@Param("q") String query, Pageable pageable);
}
