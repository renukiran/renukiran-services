package com.renukiran.repository;

import com.renukiran.entity.ApplicationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {
    @Query("SELECT DISTINCT a.id FROM ApplicationForm a JOIN a.admissions ad WHERE ad.batch.id = :batchId")
    List<Long> findApplicationFormIdsByBatchId(@Param("batchId") Long batchId);
}
