package com.renukiran.repository;

import com.renukiran.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    boolean existsByBatchName(String name);
    Optional<Batch> findByBatchName(String name);
}
