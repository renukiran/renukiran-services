package com.renukiran.service;

import com.renukiran.dto.AttendanceCandidateRowResponse;
import com.renukiran.dto.AttendanceEntryRequest;
import com.renukiran.dto.AttendancePageResponse;
import com.renukiran.dto.AttendanceSaveRequest;
import com.renukiran.dto.AttendanceSaveResponse;
import com.renukiran.dto.AttendanceForBatchResponse;
import com.renukiran.dto.CandidateAttendanceForBatchResponse;
import com.renukiran.dto.DayStatusDto;
import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Batch;
import com.renukiran.entity.CandidateAttendance;
import com.renukiran.enums.CandidateAttendanceStatus;
import com.renukiran.exception.BusinessValidationException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.AdmissionRepository;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CandidateAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final CandidateAttendanceRepository candidateAttendanceRepository;

    @Transactional(readOnly = true)
    public AttendancePageResponse getAttendancePage(Long batchId, LocalDate attendanceDate) {
        Batch batch = getBatch(batchId);
        LocalDate effectiveAttendanceDate = resolveAttendanceDate(batch, attendanceDate);

        List<Admission> batchAdmissions = getBatchAdmissions(batchId);
        List<CandidateAttendance> batchAttendance = getBatchAttendance(batchId);

        Map<Long, CandidateAttendanceStatus> todayStatusByCandidateId = new HashMap<>();
        int markedCount = 0;

        for (CandidateAttendance attendance : batchAttendance) {
            if (effectiveAttendanceDate.equals(attendance.getAttendanceDate())) {
                todayStatusByCandidateId.put(attendance.getCandidate().getId(), attendance.getAttendanceStatus());
                markedCount++;
            }
        }

        List<AttendanceCandidateRowResponse> candidates = new ArrayList<>();

        for (Admission admission : batchAdmissions) {
            ApplicationForm candidate = admission.getCandidate();
            CandidateAttendanceStatus todayStatus = todayStatusByCandidateId.get(candidate.getId());
            int attendancePercentage = calculateAttendancePercentage(candidate.getId(), batchAttendance);

            candidates.add(AttendanceCandidateRowResponse.builder()
                    .candidateId(candidate.getId())
                    .candidateName(candidate.getFullName())
                    .mobileNumber(candidate.getMobileNumber())
                    .attendancePercentage(attendancePercentage)
                    .todayStatus(todayStatus)
                    .build());
        }

        return AttendancePageResponse.builder()
                .batchId(batch.getId())
                .batchName(batch.getBatchName())
                .courseName(batch.getCourse().getCourseName())
                .attendanceDate(effectiveAttendanceDate)
                .totalClasses(getTotalClasses(batch.getStartDate(), batch.getEndDate()))
                .markedCount(markedCount)
                .enrolledCount(batchAdmissions.size())
                .candidates(candidates)
                .build();
    }

    @Transactional(readOnly = true)
    public AttendanceForBatchResponse getAttendanceForBatch(Long batchId, Long candidateId) {
        Batch batch = getBatch(batchId);

        // build list of dates between start and end inclusive
        List<LocalDate> days = new ArrayList<>();
        LocalDate cursor = batch.getStartDate();
        while (!cursor.isAfter(batch.getEndDate())) {
            days.add(cursor);
            cursor = cursor.plusDays(1);
        }

        // admissions: if candidateId provided, filter
        List<Admission> batchAdmissions = getBatchAdmissions(batchId);
        List<Admission> relevantAdmissions = new ArrayList<>();
        for (Admission admission : batchAdmissions) {
            if (candidateId == null || (admission.getCandidate() != null && candidateId.equals(admission.getCandidate().getId()))) {
                relevantAdmissions.add(admission);
            }
        }

        // attendance records for batch
        List<CandidateAttendance> batchAttendance = getBatchAttendance(batchId);

        // map candidateId -> date -> status
        Map<Long, Map<LocalDate, CandidateAttendanceStatus>> statusMap = new HashMap<>();
        for (CandidateAttendance attendance : batchAttendance) {
            if (attendance.getCandidate() == null) continue;
            Long cid = attendance.getCandidate().getId();
            if (candidateId != null && !candidateId.equals(cid)) continue;
            statusMap.computeIfAbsent(cid, k -> new HashMap<>())
                    .put(attendance.getAttendanceDate(), attendance.getAttendanceStatus());
        }

        List<CandidateAttendanceForBatchResponse> candidateResponses = new ArrayList<>();
        for (Admission admission : relevantAdmissions) {
            ApplicationForm candidate = admission.getCandidate();
            Long cid = candidate.getId();
            Map<LocalDate, CandidateAttendanceStatus> byDate = statusMap.getOrDefault(cid, Map.of());

            List<DayStatusDto> dayStatuses = new ArrayList<>();
            int presentCount = 0;
            for (LocalDate d : days) {
                CandidateAttendanceStatus s = byDate.get(d);
                String status = s == null ? "NOT_MARKED" : s.name();
                if (s == CandidateAttendanceStatus.PRESENT) presentCount++;
                dayStatuses.add(new DayStatusDto(d, status));
            }

            int totalDays = days.size();
            int percentage = totalDays == 0 ? 0 : (int) Math.round((presentCount * 100.0) / totalDays);

            candidateResponses.add(CandidateAttendanceForBatchResponse.builder()
                    .candidateId(cid)
                    .candidateName(candidate.getFullName())
                    .attendancePercentage(percentage)
                    .dayStatuses(dayStatuses)
                    .build());
        }

        return AttendanceForBatchResponse.builder()
                .batchId(batch.getId())
                .batchName(batch.getBatchName())
                .courseName(batch.getCourse().getCourseName())
                .startDate(batch.getStartDate())
                .endDate(batch.getEndDate())
                .candidateAttendances(candidateResponses)
                .build();
    }

    @Transactional
    public AttendanceSaveResponse saveAttendance(Long batchId, AttendanceSaveRequest request) {
        Batch batch = getBatch(batchId);
        LocalDate effectiveAttendanceDate = resolveAttendanceDate(batch, request.getAttendanceDate());

        List<Admission> batchAdmissions = getBatchAdmissions(batchId);
        Map<Long, ApplicationForm> admittedCandidates = new HashMap<>();
        for (Admission admission : batchAdmissions) {
            admittedCandidates.put(admission.getCandidate().getId(), admission.getCandidate());
        }

        validateEntries(request.getEntries(), admittedCandidates.keySet(), batchAdmissions.size());

        List<CandidateAttendance> existingAttendance = getBatchAttendance(batchId);
        List<CandidateAttendance> toSave = new ArrayList<>();

        for (AttendanceEntryRequest entry : request.getEntries()) {
            CandidateAttendance candidateAttendance = existingAttendance.stream()
                    .filter(attendance -> effectiveAttendanceDate.equals(attendance.getAttendanceDate()))
                    .filter(attendance -> attendance.getCandidate() != null)
                    .filter(attendance -> entry.getCandidateId().equals(attendance.getCandidate().getId()))
                    .findFirst()
                    .orElseGet(CandidateAttendance::new);

            candidateAttendance.setBatch(batch);
            candidateAttendance.setCandidate(admittedCandidates.get(entry.getCandidateId()));
            candidateAttendance.setAttendanceDate(effectiveAttendanceDate);
            candidateAttendance.setAttendanceStatus(entry.getAttendanceStatus());
            toSave.add(candidateAttendance);
        }

        candidateAttendanceRepository.saveAll(toSave);

        int markedCount = (int) getBatchAttendance(batchId).stream()
                .filter(attendance -> effectiveAttendanceDate.equals(attendance.getAttendanceDate()))
                .count();

        return AttendanceSaveResponse.builder()
                .batchId(batchId)
                .attendanceDate(effectiveAttendanceDate)
                .markedCount(markedCount)
                .enrolledCount(batchAdmissions.size())
                .message("Attendance saved successfully")
                .build();
    }

    private Batch getBatch(Long batchId) {
        return batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found", "BATCH_NOT_FOUND"));
    }

    private LocalDate resolveAttendanceDate(Batch batch, LocalDate requestedDate) {
        LocalDate effectiveDate = requestedDate != null ? requestedDate : LocalDate.now();
        if (effectiveDate.isBefore(batch.getStartDate()) || effectiveDate.isAfter(batch.getEndDate())) {
            throw new BusinessValidationException("Attendance date must be within the batch duration");
        }
        return effectiveDate;
    }

    private List<Admission> getBatchAdmissions(Long batchId) {
        return admissionRepository.findAll().stream()
                .filter(admission -> admission.getBatch() != null)
                .filter(admission -> batchId.equals(admission.getBatch().getId()))
                .sorted(Comparator.comparing(admission -> admission.getCandidate().getFullName(), String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private List<CandidateAttendance> getBatchAttendance(Long batchId) {
        return candidateAttendanceRepository.findAll().stream()
                .filter(attendance -> attendance.getBatch() != null)
                .filter(attendance -> batchId.equals(attendance.getBatch().getId()))
                .toList();
    }

    private void validateEntries(List<AttendanceEntryRequest> entries, Set<Long> admittedCandidateIds, int enrolledCount) {
        Set<Long> uniqueCandidateIds = new HashSet<>();
        for (AttendanceEntryRequest entry : entries) {
            if (!admittedCandidateIds.contains(entry.getCandidateId())) {
                throw new BusinessValidationException("Attendance can only be marked for candidates assigned to the batch");
            }
            if (!uniqueCandidateIds.add(entry.getCandidateId())) {
                throw new BusinessValidationException("Duplicate attendance entries found for the same candidate");
            }
        }

        if (entries.size() != enrolledCount) {
            throw new BusinessValidationException("Attendance must be submitted for all enrolled candidates");
        }
    }

    private int calculateAttendancePercentage(Long candidateId, List<CandidateAttendance> batchAttendance) {
        List<CandidateAttendance> candidateAttendance = batchAttendance.stream()
                .filter(attendance -> attendance.getCandidate() != null)
                .filter(attendance -> candidateId.equals(attendance.getCandidate().getId()))
                .toList();

        if (candidateAttendance.isEmpty()) {
            return 0;
        }

        long presentCount = candidateAttendance.stream()
                .filter(attendance -> attendance.getAttendanceStatus() == CandidateAttendanceStatus.PRESENT)
                .count();

        return (int) Math.round((presentCount * 100.0) / candidateAttendance.size());
    }

    private int calculateStreak(Long candidateId, LocalDate effectiveAttendanceDate, List<CandidateAttendance> batchAttendance) {
        Map<LocalDate, CandidateAttendanceStatus> statusByDate = new HashMap<>();
        for (CandidateAttendance attendance : batchAttendance) {
            if (attendance.getCandidate() != null && candidateId.equals(attendance.getCandidate().getId())) {
                statusByDate.put(attendance.getAttendanceDate(), attendance.getAttendanceStatus());
            }
        }

        int streak = 0;
        LocalDate cursor = effectiveAttendanceDate;
        while (statusByDate.get(cursor) == CandidateAttendanceStatus.PRESENT) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }



    private int getClassNumber(LocalDate startDate, LocalDate attendanceDate) {
        return Math.max(1, (int) ChronoUnit.DAYS.between(startDate, attendanceDate) + 1);
    }

    private int getTotalClasses(LocalDate startDate, LocalDate endDate) {
        return Math.max(1, (int) ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }
}
