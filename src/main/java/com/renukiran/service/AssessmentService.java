package com.renukiran.service;

import com.renukiran.dto.AssessmentCandidateRowResponse;
import com.renukiran.dto.AssessmentEntryPageResponse;
import com.renukiran.dto.AssessmentEntryRequest;
import com.renukiran.dto.AssessmentPublishResponse;
import com.renukiran.dto.AssessmentResultsCandidateResponse;
import com.renukiran.dto.AssessmentResultsPageResponse;
import com.renukiran.dto.AssessmentSaveRequest;
import com.renukiran.dto.AssessmentSaveResponse;
import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Batch;
import com.renukiran.entity.CandidateAssessment;
import com.renukiran.entity.Course;
import com.renukiran.exception.BusinessValidationException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.AdmissionRepository;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CandidateAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private static final int DEFAULT_PASS_THRESHOLD = 50;

    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final CandidateAssessmentRepository candidateAssessmentRepository;

    @Transactional(readOnly = true)
    public AssessmentEntryPageResponse getAssessmentEntryPage(Long batchId) {
        Batch batch = getBatch(batchId);
        Course course = batch.getCourse();
        List<Admission> admissions = getBatchAdmissions(batchId);
        List<CandidateAssessment> assessments = getBatchAssessments(batchId);

        AssessmentWeights weights = resolveWeights(course);
        List<AssessmentCandidateRowResponse> candidates = new ArrayList<>();
        int passCount = 0;
        int failCount = 0;
        boolean published = assessments.stream().anyMatch(assessment -> Boolean.TRUE.equals(assessment.getPublished()));

        int rowNumber = 1;
        for (Admission admission : admissions) {
            ApplicationForm candidate = admission.getCandidate();
            CandidateAssessment assessment = findAssessment(candidate.getId(), assessments);
            Double finalPercentage = calculateFinalPercentage(assessment, weights);
            String result = resolveResult(finalPercentage, course);

            if ("Pass".equals(result)) {
                passCount++;
            } else if ("Fail".equals(result)) {
                failCount++;
            }

            candidates.add(AssessmentCandidateRowResponse.builder()
                    .candidateId(candidate.getId())
                    .rowNumber(rowNumber++)
                    .candidateName(candidate.getFullName())
                    .mcqScore(assessment != null ? assessment.getMcqScore() : null)
                    .practicalScore(assessment != null ? assessment.getPracticalScore() : null)
                    .caseStudyScore(assessment != null ? assessment.getCaseStudyScore() : null)
                    .finalPercentage(finalPercentage)
                    .result(result)
                    .remarks(assessment != null ? assessment.getRemarks() : null)
                    .build());
        }

        return AssessmentEntryPageResponse.builder()
                .batchId(batchId)
                .batchName(batch.getBatchName())
                .courseName(course.getCourseName())
                .mcqWeight(weights.mcqWeight())
                .practicalWeight(weights.practicalWeight())
                .caseStudyWeight(weights.caseStudyWeight())
                .passThreshold(resolvePassThreshold(course))
                .passCount(passCount)
                .failCount(failCount)
                .passRate(calculatePassRate(passCount, admissions.size()))
                .published(published)
                .candidates(candidates)
                .build();
    }

    @Transactional(readOnly = true)
    public AssessmentResultsPageResponse getAssessmentResultsPage(Long batchId) {
        Batch batch = getBatch(batchId);
        Course course = batch.getCourse();
        List<Admission> admissions = getBatchAdmissions(batchId);
        List<CandidateAssessment> assessments = getBatchAssessments(batchId);
        AssessmentWeights weights = resolveWeights(course);

        List<AssessmentResultsCandidateResponse> candidates = new ArrayList<>();
        int totalAssessed = 0;
        int passCount = 0;
        double totalScore = 0.0;
        double highestScore = 0.0;
        boolean published = assessments.stream().anyMatch(assessment -> Boolean.TRUE.equals(assessment.getPublished()));

        int rowNumber = 1;
        for (Admission admission : admissions) {
            CandidateAssessment assessment = findAssessment(admission.getCandidate().getId(), assessments);
            Double finalPercentage = calculateFinalPercentage(assessment, weights);
            if (finalPercentage == null) {
                continue;
            }

            totalAssessed++;
            totalScore += finalPercentage;
            highestScore = Math.max(highestScore, finalPercentage);

            String result = resolveResult(finalPercentage, course);
            if ("Pass".equals(result)) {
                passCount++;
            }

            candidates.add(AssessmentResultsCandidateResponse.builder()
                    .rowNumber(rowNumber++)
                    .candidateName(admission.getCandidate().getFullName())
                    .mcqScore(assessment.getMcqScore())
                    .practicalScore(assessment.getPracticalScore())
                    .caseStudyScore(assessment.getCaseStudyScore())
                    .finalPercentage(finalPercentage)
                    .result(result)
                    .build());
        }

        return AssessmentResultsPageResponse.builder()
                .batchId(batchId)
                .batchName(batch.getBatchName())
                .courseName(course.getCourseName())
                .totalAssessed(totalAssessed)
                .passRate(calculatePassRate(passCount, totalAssessed))
                .averageScore(totalAssessed == 0 ? 0.0 : roundOneDecimal(totalScore / totalAssessed))
                .highestScore(totalAssessed == 0 ? 0.0 : roundOneDecimal(highestScore))
                .mcqWeight(weights.mcqWeight())
                .practicalWeight(weights.practicalWeight())
                .caseStudyWeight(weights.caseStudyWeight())
                .passThreshold(resolvePassThreshold(course))
                .published(published)
                .candidates(candidates)
                .build();
    }

    @Transactional
    public AssessmentSaveResponse saveAssessments(Long batchId, AssessmentSaveRequest request) {
        Batch batch = getBatch(batchId);
        Course course = batch.getCourse();
        List<Admission> admissions = getBatchAdmissions(batchId);
        Map<Long, ApplicationForm> admittedCandidates = mapAdmittedCandidates(admissions);
        validateEntries(request.getEntries(), admittedCandidates.keySet());

        List<CandidateAssessment> existingAssessments = getBatchAssessments(batchId);
        List<CandidateAssessment> assessmentsToSave = new ArrayList<>();

        for (AssessmentEntryRequest entry : request.getEntries()) {
            CandidateAssessment assessment = findAssessment(entry.getCandidateId(), existingAssessments);
            if (assessment == null) {
                assessment = new CandidateAssessment();
                assessment.setBatch(batch);
                assessment.setCandidate(admittedCandidates.get(entry.getCandidateId()));
                assessment.setPublished(Boolean.FALSE);
            }

            assessment.setMcqScore(entry.getMcqScore());
            assessment.setPracticalScore(entry.getPracticalScore());
            assessment.setCaseStudyScore(entry.getCaseStudyScore());
            assessment.setRemarks(entry.getRemarks());
            if (assessment.getPublished() == null) {
                assessment.setPublished(Boolean.FALSE);
            }
            assessmentsToSave.add(assessment);
        }

        candidateAssessmentRepository.saveAll(assessmentsToSave);

        AssessmentWeights weights = resolveWeights(course);
        List<CandidateAssessment> allAssessments = getBatchAssessments(batchId);
        int passCount = 0;
        int failCount = 0;
        for (CandidateAssessment assessment : allAssessments) {
            String result = resolveResult(calculateFinalPercentage(assessment, weights), course);
            if ("Pass".equals(result)) {
                passCount++;
            } else if ("Fail".equals(result)) {
                failCount++;
            }
        }

        return AssessmentSaveResponse.builder()
                .batchId(batchId)
                .savedCount(assessmentsToSave.size())
                .passCount(passCount)
                .failCount(failCount)
                .passRate(calculatePassRate(passCount, admissions.size()))
                .message("Assessments saved successfully")
                .build();
    }

    @Transactional
    public AssessmentPublishResponse publishAssessments(Long batchId) {
        getBatch(batchId);
        List<CandidateAssessment> assessments = getBatchAssessments(batchId);
        if (assessments.isEmpty()) {
            throw new BusinessValidationException("No assessment entries available to publish");
        }

        LocalDateTime publishedAt = LocalDateTime.now();
        for (CandidateAssessment assessment : assessments) {
            assessment.setPublished(Boolean.TRUE);
            assessment.setPublishedAt(publishedAt);
        }
        candidateAssessmentRepository.saveAll(assessments);

        return AssessmentPublishResponse.builder()
                .batchId(batchId)
                .publishedCount(assessments.size())
                .published(Boolean.TRUE)
                .message("Assessment results published successfully")
                .build();
    }

    private Batch getBatch(Long batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found", "BATCH_NOT_FOUND"));
    }

    private List<Admission> getBatchAdmissions(Long batchId) {
        return admissionRepository.findAll().stream()
                .filter(admission -> admission.getBatch() != null)
                .filter(admission -> batchId.equals(admission.getBatch().getId()))
                .sorted(Comparator.comparing(admission -> admission.getCandidate().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private Map<Long, ApplicationForm> mapAdmittedCandidates(List<Admission> admissions) {
        Map<Long, ApplicationForm> admittedCandidates = new HashMap<>();
        for (Admission admission : admissions) {
            admittedCandidates.put(admission.getCandidate().getId(), admission.getCandidate());
        }
        return admittedCandidates;
    }

    private List<CandidateAssessment> getBatchAssessments(Long batchId) {
        return candidateAssessmentRepository.findAll().stream()
                .filter(assessment -> assessment.getBatch() != null)
                .filter(assessment -> batchId.equals(assessment.getBatch().getId()))
                .toList();
    }

    private CandidateAssessment findAssessment(Long candidateId, List<CandidateAssessment> assessments) {
        return assessments.stream()
                .filter(assessment -> assessment.getCandidate() != null)
                .filter(assessment -> candidateId.equals(assessment.getCandidate().getId()))
                .findFirst()
                .orElse(null);
    }

    private void validateEntries(List<AssessmentEntryRequest> entries, Set<Long> admittedCandidateIds) {
        Set<Long> seenCandidateIds = new HashSet<>();
        for (AssessmentEntryRequest entry : entries) {
            if (!admittedCandidateIds.contains(entry.getCandidateId())) {
                throw new BusinessValidationException("Assessments can only be entered for candidates assigned to the batch");
            }
            if (!seenCandidateIds.add(entry.getCandidateId())) {
                throw new BusinessValidationException("Duplicate assessment entries found for the same candidate");
            }
        }
    }

    private AssessmentWeights resolveWeights(Course course) {
        int mcqWeight = parseWeight(course.getMcqAssessment());
        int practicalWeight = parseWeight(course.getPracticalAssessment());
        int caseStudyWeight = parseWeight(course.getCaseStudyAssessment());
        int totalWeight = mcqWeight + practicalWeight + caseStudyWeight;

        if (totalWeight <= 0) {
            throw new BusinessValidationException("Course assessment weights are not configured");
        }

        return new AssessmentWeights(mcqWeight, practicalWeight, caseStudyWeight);
    }

    private int parseWeight(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            return 0;
        }

        return Integer.parseInt(digits);
    }

    private Double calculateFinalPercentage(CandidateAssessment assessment, AssessmentWeights weights) {
        if (assessment == null
                || assessment.getMcqScore() == null
                || assessment.getPracticalScore() == null
                || assessment.getCaseStudyScore() == null) {
            return null;
        }

        int totalWeight = weights.mcqWeight() + weights.practicalWeight() + weights.caseStudyWeight();
        if (totalWeight <= 0) {
            return null;
        }

        double weightedScore = (assessment.getMcqScore() * weights.mcqWeight())
                + (assessment.getPracticalScore() * weights.practicalWeight())
                + (assessment.getCaseStudyScore() * weights.caseStudyWeight());

        return Math.round((weightedScore / totalWeight) * 10.0) / 10.0;
    }

    private Double calculatePassRate(int passCount, int totalCandidates) {
        if (totalCandidates <= 0) {
            return 0.0;
        }
        return roundOneDecimal((passCount * 100.0) / totalCandidates);
    }

    private Double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private Integer resolvePassThreshold(Course course) {
        return course.getPassThreshold() != null ? course.getPassThreshold() : DEFAULT_PASS_THRESHOLD;
    }

    private String resolveResult(Double finalPercentage, Course course) {
        if (finalPercentage == null) {
            return null;
        }
        return finalPercentage >= resolvePassThreshold(course) ? "Pass" : "Fail";
    }

    private record AssessmentWeights(int mcqWeight, int practicalWeight, int caseStudyWeight) {
    }
}
