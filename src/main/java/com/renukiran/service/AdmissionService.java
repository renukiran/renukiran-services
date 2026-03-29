package com.renukiran.service;

import com.renukiran.dto.AdmissionRequest;
import com.renukiran.dto.AdmissionResponse;
import com.renukiran.entity.*;
import com.renukiran.exception.BusinessValidationException;
import com.renukiran.exception.DuplicateResourceException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdmissionService {
    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final AdmissionSequenceRepository sequenceRepository;
    private final ApplicationFormRepository applicationFormRepository;

    @Transactional
    public AdmissionResponse registerAdmission(AdmissionRequest request){
        // Candidate handling
        ApplicationForm candidate = applicationFormRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found","COURSE_NOT_FOUND"));

        // Course validation
        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found","COURSE_NOT_FOUND"));

        //Same course check
        boolean alreadyEnrolled = admissionRepository.existsByCandidateAndBatch(candidate, batch);

        if (alreadyEnrolled) {
            throw new DuplicateResourceException("Candidate already enrolled for this course","ALREADY_ENROLLED");
        }


        // Generate admission number
        String admissionNumber = generateAdmissionNumber();

        // Create admission
        Admission admission = new Admission();
        admission.setAdmissionNumber(admissionNumber);
        admission.setCandidate(candidate);
        admission.setBatch(batch);

        try {
            admissionRepository.save(admission);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("Duplicate admission: course or batch/timing conflict","DUPLICATE_ADMISSON");
        }
        return new AdmissionResponse(admissionNumber, "Admission created successfully");
    }


    private String generateAdmissionNumber() {

        int year = LocalDate.now().getYear();

        AdmissionSequence seq = sequenceRepository.findById(year)
                .orElseGet(() -> {
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

}
