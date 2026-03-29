package com.renukiran.repository;

import com.renukiran.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    boolean existsByCandidateAndBatch(ApplicationForm applicationForm, Batch batch);
}
