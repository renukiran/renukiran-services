package com.renukiran.repository;

import com.renukiran.entity.CandidateAttendance;
import com.renukiran.enums.CandidateAttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateAttendanceRepository extends JpaRepository<CandidateAttendance, Long> {
    long countByBatch_IdAndCandidate_IdAndAttendanceStatus(Long batchId, Long candidateId, CandidateAttendanceStatus status);
}
