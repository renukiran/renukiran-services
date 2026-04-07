package com.renukiran.controllers;

import com.renukiran.dto.AssessmentEntryPageResponse;
import com.renukiran.dto.AssessmentPublishResponse;
import com.renukiran.dto.AssessmentResultsPageResponse;
import com.renukiran.dto.AssessmentSaveRequest;
import com.renukiran.dto.AssessmentSaveResponse;
import com.renukiran.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/batches/{batchId}/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService assessmentService;

    @GetMapping
    public ResponseEntity<AssessmentEntryPageResponse> getAssessmentEntryPage(@PathVariable Long batchId) {
        return ResponseEntity.ok(assessmentService.getAssessmentEntryPage(batchId));
    }

    @GetMapping("/results")
    public ResponseEntity<AssessmentResultsPageResponse> getAssessmentResultsPage(@PathVariable Long batchId) {
        return ResponseEntity.ok(assessmentService.getAssessmentResultsPage(batchId));
    }

    @PostMapping
    public ResponseEntity<AssessmentSaveResponse> saveAssessments(
            @PathVariable Long batchId,
            @Valid @RequestBody AssessmentSaveRequest request) {
        return ResponseEntity.ok(assessmentService.saveAssessments(batchId, request));
    }

    @PostMapping("/publish")
    public ResponseEntity<AssessmentPublishResponse> publishAssessments(@PathVariable Long batchId) {
        return ResponseEntity.ok(assessmentService.publishAssessments(batchId));
    }
}
