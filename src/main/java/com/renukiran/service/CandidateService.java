package com.renukiran.service;

import com.renukiran.dto.CandidateDetailResponse;
import com.renukiran.dto.CandidateResponse;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.ApplicationFormRepository;
import com.renukiran.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final ApplicationFormRepository applicationFormRepository;
    private final BatchRepository batchRepository;

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

        return applicationFormRepository.findApplicationFormIdsByBatchId(batchId).stream()
                .distinct()
                .map(applicationFormRepository::findById)
                .flatMap(java.util.Optional::stream)
                .sorted(Comparator.comparing(ApplicationForm::getFullName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(CandidateResponse::from)
                .toList();
    }
}