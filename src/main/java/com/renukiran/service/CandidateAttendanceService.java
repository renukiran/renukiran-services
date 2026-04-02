package com.renukiran.service;

import com.renukiran.dto.CandidateAttendanceEntryRequest;
import com.renukiran.dto.CandidateAttendanceEntryResponse;
import com.renukiran.dto.CandidateAttendanceRequest;
import com.renukiran.dto.CandidateAttendanceResponse;
import com.renukiran.dto.CandidateAttendanceSheetResponse;
import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Batch;
import com.renukiran.entity.CandidateAttendance;
import com.renukiran.exception.BusinessValidationException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.AdmissionRepository;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CandidateAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CandidateAttendanceService {

    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final CandidateAttendanceRepository candidateAttendanceRepository;

    @Transactional
    public CandidateAttendanceResponse markAttendance(CandidateAttendanceRequest request) {
        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found", "BATCH_NOT_FOUND"));

        validateAttendanceDate(batch, request.getAttendanceDate());

        List<Admission> batchAdmissions = admissionRepository.findAll().stream()
                .filter(admission -> admission.getBatch() != null)
                .filter(admission -> batch.getId().equals(admission.getBatch().getId()))
                .toList();

        if (batchAdmissions.isEmpty()) {
            throw new BusinessValidationException("No candidates are enrolled in this batch");
        }

        Map<Long, ApplicationForm> admittedCandidates = new HashMap<>();
        for (Admission admission : batchAdmissions) {
            admittedCandidates.put(admission.getCandidate().getId(), admission.getCandidate());
        }

        validateAttendanceEntries(request.getAttendanceEntries(), admittedCandidates.keySet(), batchAdmissions.size());

        List<CandidateAttendance> allAttendanceRecords = candidateAttendanceRepository.findAll();
        List<CandidateAttendance> recordsToSave = new ArrayList<>();
        int presentCount = 0;

        for (CandidateAttendanceEntryRequest entry : request.getAttendanceEntries()) {
            if (Boolean.TRUE.equals(entry.getPresent())) {
                presentCount++;
            }

            CandidateAttendance attendance = allAttendanceRecords.stream()
                    .filter(record -> record.getBatch() != null && batch.getId().equals(record.getBatch().getId()))
                    .filter(record -> record.getCandidate() != null && entry.getCandidateId().equals(record.getCandidate().getId()))
                    .filter(record -> request.getAttendanceDate().equals(record.getAttendanceDate()))
                    .findFirst()
                    .orElseGet(CandidateAttendance::new);

            attendance.setBatch(batch);
            attendance.setCandidate(admittedCandidates.get(entry.getCandidateId()));
            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setPresent(entry.getPresent());
            recordsToSave.add(attendance);
        }

        candidateAttendanceRepository.saveAll(recordsToSave);

        return CandidateAttendanceResponse.builder()
                .batchId(batch.getId())
                .batchName(batch.getBatchName())
                .attendanceDate(request.getAttendanceDate())
                .totalCandidates(batchAdmissions.size())
                .presentCount(presentCount)
                .absentCount(batchAdmissions.size() - presentCount)
                .message("Attendance saved successfully")
                .build();
    }

    @Transactional(readOnly = true)
    public CandidateAttendanceSheetResponse getAttendanceSheet(Long batchId, LocalDate attendanceDate) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found", "BATCH_NOT_FOUND"));

        validateAttendanceDate(batch, attendanceDate);

        List<Admission> batchAdmissions = admissionRepository.findAll().stream()
                .filter(admission -> admission.getBatch() != null)
                .filter(admission -> batchId.equals(admission.getBatch().getId()))
                .sorted(Comparator.comparing(admission -> admission.getCandidate().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .toList();

        List<CandidateAttendance> attendanceRecords = candidateAttendanceRepository.findAll().stream()
                .filter(record -> record.getBatch() != null)
                .filter(record -> batchId.equals(record.getBatch().getId()))
                .filter(record -> attendanceDate.equals(record.getAttendanceDate()))
                .toList();

        Map<Long, Boolean> attendanceByCandidateId = new HashMap<>();
        for (CandidateAttendance attendanceRecord : attendanceRecords) {
            attendanceByCandidateId.put(attendanceRecord.getCandidate().getId(), attendanceRecord.getPresent());
        }

        return CandidateAttendanceSheetResponse.builder()
                .batchId(batch.getId())
                .batchName(batch.getBatchName())
                .attendanceDate(attendanceDate)
                .attendanceMarked(batchAdmissions.size() > 0 && attendanceRecords.size() >= batchAdmissions.size())
                .candidates(batchAdmissions.stream()
                        .map(admission -> CandidateAttendanceEntryResponse.builder()
                                .candidateId(admission.getCandidate().getId())
                                .candidateName(admission.getCandidate().getFullName())
                                .present(attendanceByCandidateId.get(admission.getCandidate().getId()))
                                .build())
                        .toList())
                .build();
    }

    private void validateAttendanceDate(Batch batch, LocalDate attendanceDate) {
        if (attendanceDate.isBefore(batch.getStartDate()) || attendanceDate.isAfter(batch.getEndDate())) {
            throw new BusinessValidationException("Attendance date must be within the batch duration");
        }
    }

    private void validateAttendanceEntries(List<CandidateAttendanceEntryRequest> attendanceEntries,
                                           Set<Long> admittedCandidateIds,
                                           int enrolledCandidatesCount) {
        Set<Long> submittedCandidateIds = new HashSet<>();

        for (CandidateAttendanceEntryRequest attendanceEntry : attendanceEntries) {
            if (!admittedCandidateIds.contains(attendanceEntry.getCandidateId())) {
                throw new BusinessValidationException("Attendance can be marked only for enrolled candidates");
            }

            if (!submittedCandidateIds.add(attendanceEntry.getCandidateId())) {
                throw new BusinessValidationException("Duplicate candidate entries found in attendance request");
            }
        }

        if (attendanceEntries.size() != enrolledCandidatesCount) {
            throw new BusinessValidationException("Attendance must be submitted for all enrolled candidates");
        }
    }
}
