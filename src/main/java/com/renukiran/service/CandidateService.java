package com.renukiran.service;

import com.renukiran.dto.CandidateDetailResponse;
import com.renukiran.dto.CandidateResponse;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Admission;
import com.renukiran.entity.Batch;
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
                .peek(this::populateAdmissionAttendancePercentages)
                .map(af -> {
                    Integer overall = maxAttendanceAcrossAdmissions(af);
                    return CandidateResponse.from(af, overall);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public CandidateDetailResponse getCandidateById(Long id) {
        ApplicationForm candidate = applicationFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found", "CANDIDATE_NOT_FOUND"));
        // ensure per-admission attendance percentages are calculated before mapping to DTO
        populateAdmissionAttendancePercentages(candidate);
        return CandidateDetailResponse.from(candidate);
    }

    @Transactional(readOnly = true)
    public List<CandidateResponse> getCandidatesByBatchId(Long batchId) {
        if (!batchRepository.existsById(batchId)) {
            throw new ResourceNotFoundException("Batch not found", "BATCH_NOT_FOUND");
        }

        // compute total days for batch (inclusive)
        LocalDate start = batchRepository.findById(batchId).map(Batch::getStartDate).orElse(null);
        LocalDate end = batchRepository.findById(batchId).map(Batch::getEndDate).orElse(null);
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
                .peek(this::populateAdmissionAttendancePercentages) // ensure per-admission percentages are computed
                .map(af -> CandidateResponse.from(af, getPercentage(batchId, af, denominator)))
                .toList();
    }

    private int getPercentage(Long batchId, ApplicationForm af, int denominator) {
        if (denominator == 0) return 0;
        long presentCount = candidateAttendanceRepository
                .countByBatch_IdAndCandidate_IdAndAttendanceStatus(batchId, af.getId(), CandidateAttendanceStatus.PRESENT);
        return (int) Math.round((presentCount * 100.0) / denominator);
    }

    // Populate attendancePercentage on each Admission of the application form (transient, not persisted)
    private void populateAdmissionAttendancePercentages(ApplicationForm af) {
        if (af == null || af.getAdmissions() == null) return;
        LocalDate today = LocalDate.now();
        for (Admission admission : af.getAdmissions()) {
            if (admission == null || admission.getBatch() == null) continue;
            try {
                Long batchId = admission.getBatch().getId();
                if (batchId == null) {
                    admission.setAttendancePercentage(null);
                    continue;
                }
                LocalDate start = admission.getBatch().getStartDate();
                LocalDate end = admission.getBatch().getEndDate();
                LocalDate effectiveEnd = (end == null) ? null : (end.isAfter(today) ? today : end);
                int totalDays = 0;
                if (start != null && effectiveEnd != null && !effectiveEnd.isBefore(start)) {
                    totalDays = (int) ChronoUnit.DAYS.between(start, effectiveEnd) + 1;
                }
                Integer percentage = null;
                if (totalDays > 0) {
                    long present = candidateAttendanceRepository
                            .countByBatch_IdAndCandidate_IdAndAttendanceStatus(batchId, af.getId(), CandidateAttendanceStatus.PRESENT);
                    percentage = (int) Math.round((present * 100.0) / totalDays);
                }
                admission.setAttendancePercentage(percentage);
            } catch (Exception e) {
                // On errors, set null and continue
                admission.setAttendancePercentage(null);
            }
        }
    }

    private Integer maxAttendanceAcrossAdmissions(ApplicationForm af) {
        if (af == null || af.getAdmissions() == null || af.getAdmissions().isEmpty()) return null;
        Integer max = null;
        for (Admission admission : af.getAdmissions()) {
            if (admission == null) continue;
            Integer p = admission.getAttendancePercentage();
            if (p != null) {
                if (max == null || p > max) max = p;
            }
        }
        return max;
    }
}