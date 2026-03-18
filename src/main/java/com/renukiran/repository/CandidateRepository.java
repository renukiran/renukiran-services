package com.renukiran.repository;

import com.renukiran.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByMobile(String mobile);
    Optional<Candidate> findByNameAndDobAndMobile(String name, LocalDate dob, String mobile);
}
