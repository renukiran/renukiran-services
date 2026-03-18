package com.renukiran.repository;

import com.renukiran.entity.Admission;
import com.renukiran.entity.Candidate;
import com.renukiran.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    boolean existsByCandidateAndCourse(Candidate candidate, Course course);
    boolean existsByCandidateAndBatchNoAndTiming(Candidate candidate, String batchNo, String timing);
}
