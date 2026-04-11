package com.renukiran.controllers;

import com.renukiran.dto.CandidateAttendanceRequest;
import com.renukiran.dto.CandidateAttendanceResponse;
import com.renukiran.dto.CandidateAttendanceSheetResponse;
import com.renukiran.service.CandidateAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/candidate-attendance")
@RequiredArgsConstructor
@Deprecated
public class CandidateAttendanceController {

    private final CandidateAttendanceService candidateAttendanceService;

    @PostMapping
    public ResponseEntity<CandidateAttendanceResponse> markAttendance(@Valid @RequestBody CandidateAttendanceRequest request) {
        return ResponseEntity.ok(candidateAttendanceService.markAttendance(request));
    }

    @GetMapping("/batches/{batchId}")
    public ResponseEntity<CandidateAttendanceSheetResponse> getAttendanceSheet(
            @PathVariable Long batchId,
            @RequestParam LocalDate attendanceDate) {
        return ResponseEntity.ok(candidateAttendanceService.getAttendanceSheet(batchId, attendanceDate));
    }
}
