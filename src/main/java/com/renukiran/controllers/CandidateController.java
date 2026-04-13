package com.renukiran.controllers;

import com.renukiran.dto.CandidateDetailResponse;
import com.renukiran.dto.CandidateResponse;
import com.renukiran.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateDetailResponse> getCandidateById(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }

    @GetMapping("/batches/{batchId}/candidates")
    public ResponseEntity<List<CandidateResponse>> getCandidatesByBatchId(@PathVariable Long batchId) {
        return ResponseEntity.ok(candidateService.getCandidatesByBatchId(batchId));
    }
}
