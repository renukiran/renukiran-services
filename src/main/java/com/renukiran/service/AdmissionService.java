package com.renukiran.service;

import com.renukiran.dto.AdmissionRequest;
import com.renukiran.dto.AdmissionResponse;
import com.renukiran.entity.Admission;
import com.renukiran.entity.AdmissionSequence;
import com.renukiran.entity.Candidate;
import com.renukiran.entity.Course;
import com.renukiran.repository.AdmissionRepository;
import com.renukiran.repository.AdmissionSequenceRepository;
import com.renukiran.repository.CandidateRepository;
import com.renukiran.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdmissionService {
    private final CandidateRepository candidateRepository;
    private final CourseRepository courseRepository;
    private final AdmissionRepository admissionRepository;
    private final AdmissionSequenceRepository sequenceRepository;

    @Transactional
    public AdmissionResponse registerAdmission(AdmissionRequest request){
        // Candidate handling
        Candidate candidate = createOrFetchCandidate(request);

        // Course validation
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        //Same course check
        boolean alreadyEnrolled = admissionRepository.existsByCandidateAndCourse(candidate, course);

        if (alreadyEnrolled) {
            throw new RuntimeException("Candidate already enrolled for this course");
        }
        // Same batch + timing check (NEW)
        if (admissionRepository.existsByCandidateAndBatchNoAndTiming(candidate, request.getBatchNo(), request.getTiming())) {
            throw new RuntimeException("Candidate already has a course in the same batch and timing");
        }

        // Generate admission number
        String admissionNumber = generateAdmissionNumber();

        // Create admission
        Admission admission = new Admission();
        admission.setAdmissionNumber(admissionNumber);
        admission.setCandidate(candidate);
        admission.setCourse(course);
        admission.setBatchNo(request.getBatchNo());
        admission.setTiming(request.getTiming());
        admission.setAdmissionDate(LocalDate.now());
        try {
            admissionRepository.save(admission);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Duplicate admission: course or batch/timing conflict");
        }
        return new AdmissionResponse(admissionNumber, "Admission created successfully");
    }
    private Candidate createOrFetchCandidate(AdmissionRequest request) {

        return candidateRepository.findByMobile(request.getMobile())
                .map(existing -> {
                    boolean match = existing.getName().equalsIgnoreCase(request.getName()) &&
                                    existing.getDob().equals(request.getDob());

                    if (!match) {
                        throw new RuntimeException("Mobile already registered with different name/DOB");
                    }

                    // update optional fields
                    existing.setAddress(request.getAddress());
                    existing.setSkills(request.getSkills());
                    existing.setQualification(request.getQualification());

                    return existing;
                })
                .orElseGet(() -> {
                    Candidate c = new Candidate();
                    c.setName(request.getName());
                    c.setDob(request.getDob());
                    c.setMobile(request.getMobile());
                    c.setGender(request.getGender());
                    c.setGuardianName(request.getGuardianName());
                    c.setQualification(request.getQualification());
                    c.setOccupation(request.getOccupation());
                    c.setNationality(request.getNationality());
                    c.setReligion(request.getReligion());
                    c.setSkills(request.getSkills());
                    c.setCategory(request.getCategory());
                    c.setAddress(request.getAddress());
                    c.setReferenceName(request.getReferenceName());
                    return candidateRepository.save(c);
                });
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
