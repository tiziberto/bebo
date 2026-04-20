package com.bemo.backend.repository;

import com.bemo.backend.model.Estudio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EstudioRepository extends JpaRepository<Estudio, Long> {
    List<Estudio> findByActivoTrue();

    @Query("SELECT e FROM Estudio e WHERE LOWER(e.nombre) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Estudio> searchPage(@Param("q") String query, Pageable pageable);
}
