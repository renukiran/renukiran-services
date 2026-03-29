package com.renukiran.repository;

import com.renukiran.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementRepository extends JpaRepository<Placement, Long> {
    List<Placement> findByStatus(String status);
    long countByStatus(String status);
}
