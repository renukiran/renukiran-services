package com.renukiran.service;

import com.renukiran.dto.CandidateDetailResponse;
import com.renukiran.dto.CandidateResponse;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.ApplicationFormRepository;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CandidateAttendanceRepository;
import com.renukiran.enums.CandidateAttendanceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final ApplicationFormRepository applicationFormRepository;
    private final BatchRepository batchRepository;
    private final CandidateAttendanceRepository candidateAttendanceRepository;

    @Transactional(readOnly = true)
    public List<CandidateResponse> getAllCandidates() {
        return applicationFormRepository.findAll().stream()
                .sorted(Comparator.comparing(ApplicationForm::getFullName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(CandidateResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CandidateDetailResponse getCandidateById(Long id) {
        ApplicationForm candidate = applicationFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found", "CANDIDATE_NOT_FOUND"));
        return CandidateDetailResponse.from(candidate);
    }

    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidatesByBatchId(Long batchId) {
        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found", "BATCH_NOT_FOUND");
        }

        // compute total days for batch (inclusive)
        LocalDate start = batchRepository.findById(batchId).map(b -> b.getStartDate()).orElse(null);
        LocalDate end = batchRepository.findById(batchId).map(b -> b.getEndDate()).orElse(null);
        int totalDays = 0;
        if (start != null && end != null) {
            totalDays = (int) ChronoUnit.DAYS.between(start, end) + 1;
        }
        final int denominator = totalDays;

        return applicationFormRepository.findApplicationFormIdsByBatchId(batchId).stream()
                .distinct()
                .map(applicationFormRepository::findById)
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(ApplicationForm::getFullName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(af -> {
                    long presentCount = 0;
                    if (denominator > 0) {
                        presentCount = candidateAttendanceRepository
                                .countByBatch_IdAndCandidate_IdAndAttendanceStatus(batchId, af.getId(), CandidateAttendanceStatus.PRESENT);
                    }
                    int percentage = denominator == 0 ? 0 : (int) Math.round((presentCount * 100.0) / denominator);
                    return CandidateResponse.from(af, percentage);
                })
                .toList();
    }
}