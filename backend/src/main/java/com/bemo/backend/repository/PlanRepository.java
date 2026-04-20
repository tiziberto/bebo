package com.bemo.backend.repository;

import com.bemo.backend.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByObraSocialId(Long obraSocialId);
}
