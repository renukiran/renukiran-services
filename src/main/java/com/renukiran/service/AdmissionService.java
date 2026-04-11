package com.renukiran.service;

import com.renukiran.dto.AdmissionRequest;
import com.renukiran.dto.AdmissionResponse;
import com.renukiran.entity.*;
import com.renukiran.enums.AdmissionStatus;
import com.renukiran.exception.DuplicateResourceException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdmissionService {
    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final AdmissionSequenceRepository sequenceRepository;
    private final ApplicationFormRepository applicationFormRepository;

    private static final Logger log = LoggerFactory.getLogger(AdmissionService.class);

    @Transactional
    public AdmissionResponse registerAdmission(AdmissionRequest request) {
        // Candidate handling
        ApplicationForm candidate = applicationFormRepository.findById(request.getCandidateId()).orElseThrow(() -> new ResourceNotFoundException("Candidate not found", "COURSE_NOT_FOUND"));

        // Course validation
        Batch batch = batchRepository.findById(request.getBatchId()).orElseThrow(() -> new ResourceNotFoundException("Batch not found", "COURSE_NOT_FOUND"));

        //Same course check
        boolean alreadyEnrolled = admissionRepository.existsByCandidateAndBatch(candidate, batch);

        if (alreadyEnrolled) {
            throw new DuplicateResourceException("Candidate already enrolled for this course", "ALREADY_ENROLLED");
        }


        // Generate admission number
        String admissionNumber = generateAdmissionNumber();

        // Create admission
        Admission admission = new Admission();
        admission.setAdmissionNumber(admissionNumber);
        admission.setCandidate(candidate);
        admission.setBatch(batch);
        admission.setStatus(AdmissionStatus.ASSIGNED_TO_BATCH);
        try {
            admissionRepository.save(admission);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Duplicate admission: course or batch/timing conflict", "DUPLICATE_ADMISSON");
        }
        return new AdmissionResponse(admissionNumber, "Admission created successfully");
    }


    private String generateAdmissionNumber() {

        int year = LocalDate.now().getYear();

        AdmissionSequence seq = sequenceRepository.findById(year).orElseGet(() -> {
            AdmissionSequence s = new AdmissionSequence();
            s.setSeqYear(year);
            s.setLastNumber(0);
            return s;
        });

        int next = seq.getLastNumber() + 1;
        seq.setLastNumber(next);

        sequenceRepository.save(seq);

        return "ADM-" + year + "-" + String.format("%04d", next);
    }


    public void updateApplicationStatusBasedOnBatchDates(Admission admission) {
        if (admission == null) return;
        LocalDate today = LocalDate.now();
        Batch batch = admission.getBatch();
        LocalDate start = batch.getStartDate();
        LocalDate end = batch.getEndDate();


        AdmissionStatus newStatus = AdmissionStatus.ASSIGNED_TO_BATCH;
        if (start != null && end != null) {
            if ((start.isEqual(today) || start.isBefore(today)) && (end.isEqual(today) || end.isAfter(today))) {
                newStatus = AdmissionStatus.TRAINING_STARTED;
            } else if (end.isBefore(today)) {
                newStatus = AdmissionStatus.TRAINING_COMPLETED;
            } else {
                newStatus = AdmissionStatus.ASSIGNED_TO_BATCH;
            }
        }

        // Only update if different
        try {
            if (admission.getStatus() != newStatus) {
                admission.setStatus(newStatus);
                admissionRepository.save(admission);
                log.info("Updated  status for Admission id={} to {}", admission.getAdmissionNumber(), newStatus);
            } else {
                log.debug("No status change required for Admission id={}", admission.getAdmissionNumber());
            }
        } catch (Exception e) {
            log.warn("Failed to persist updated  status for Admission id={}", admission.getAdmissionNumber(), e);
        }
    }


}
