package com.renukiran.controllers;

import com.renukiran.dto.CandidateResponse;
import com.renukiran.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateRepository candidateRepository;

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
        List<CandidateResponse> candidates = candidateRepository.findAll()
                .stream()
                .map(CandidateResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateResponse> getCandidateById(@PathVariable Long id) {
        return candidateRepository.findById(id)
                .map(CandidateResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
