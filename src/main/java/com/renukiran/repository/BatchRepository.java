package com.renukiran.repository;

import com.renukiran.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    Optional<Batch> findByBatchCode(String batchCode);
    boolean existsByBatchCode(String batchCode);
    long countByStatus(String status);
}
