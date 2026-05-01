package com.renukiran.controllers;

import com.renukiran.dto.AttendancePageResponse;
import com.renukiran.dto.AttendanceSaveRequest;
import com.renukiran.dto.AttendanceSaveResponse;
import com.renukiran.dto.AttendanceForBatchResponse;
import com.renukiran.service.AttendanceService;
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
@RequestMapping("/api/v1/batches/{batchId}/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<AttendancePageResponse> getAttendancePage(
            @PathVariable Long batchId,
            @RequestParam(required = false) LocalDate attendanceDate) {
        return ResponseEntity.ok(attendanceService.getAttendancePage(batchId, attendanceDate));
    }

    /**
     * Returns attendance across the whole batch duration (list of days + per-candidate day statuses).
     * Optional candidateId filters to a single candidate.
     */
    @GetMapping("/summary")
    public ResponseEntity<AttendanceForBatchResponse> getAttendanceForBatch(
            @PathVariable Long batchId,
            @RequestParam(required = false) Long candidateId) {
        return ResponseEntity.ok(attendanceService.getAttendanceForBatch(batchId, candidateId));
    }

    @PostMapping
    public ResponseEntity<AttendanceSaveResponse> saveAttendance(
            @PathVariable Long batchId,
            @Valid @RequestBody AttendanceSaveRequest request) {
        return ResponseEntity.ok(attendanceService.saveAttendance(batchId, request));
    }
}
