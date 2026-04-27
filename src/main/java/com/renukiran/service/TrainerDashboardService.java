package com.renukiran.service;

import com.renukiran.dto.TrainerBatchCardResponse;
import com.renukiran.dto.TrainerDashboardResponse;
import com.renukiran.dto.TrainerBatchLowAttendanceAlertResponse;
import com.renukiran.entity.*;
import com.renukiran.enums.CandidateAttendanceStatus;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerDashboardService {

    private final SignUpRepository userRepository;
    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final CandidateAttendanceRepository candidateAttendanceRepository;

    @Transactional(readOnly = true)
    public TrainerDashboardResponse getDashboard(Long trainerId) {
        Users trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found", "TRAINER_NOT_FOUND"));

        LocalDate today = LocalDate.now();
        List<Batch> activeBatches = batchRepository.findAll().stream()
                .filter(batch -> batch.getTrainer() != null && trainerId.equals(batch.getTrainer().getId()))
                .filter(batch -> !batch.getStartDate().isAfter(today) && !batch.getEndDate().isBefore(today))
                .sorted((first, second) -> {
                    int startDateComparison = first.getStartDate().compareTo(second.getStartDate());
                    if (startDateComparison != 0) {
                        return startDateComparison;
                    }
                    return first.getId().compareTo(second.getId());
                })
                .toList();

        List<Admission> admissions = admissionRepository.findAll().stream()
                .filter(admission -> admission.getBatch() != null)
                .filter(admission -> admission.getBatch().getTrainer() != null)
                .filter(admission -> trainerId.equals(admission.getBatch().getTrainer().getId()))
                .toList();

        Map<Long, Long> batchEnrollmentCounts = admissions.stream()
                .collect(Collectors.groupingBy(admission -> admission.getBatch().getId(), Collectors.counting()));

        List<CandidateAttendance> attendanceRecords = candidateAttendanceRepository.findAll().stream()
                .filter(attendance -> attendance.getBatch() != null)
                .filter(attendance -> attendance.getBatch().getTrainer() != null)
                .filter(attendance -> trainerId.equals(attendance.getBatch().getTrainer().getId()))
                .toList();

        return TrainerDashboardResponse.builder()
                .trainerId(trainer.getId())
                .trainerName(trainer.getFirstName() + " " + trainer.getLastName())
                .generatedOn(today)
                .activeBatches(activeBatches.stream()
                        .map(batch -> mapBatchCard(batch, today, batchEnrollmentCounts, attendanceRecords))
                        .toList())
                .lowAttendanceAlerts(buildLowAttendanceAlerts(activeBatches, admissions, attendanceRecords))
                .build();
    }

    private TrainerBatchCardResponse mapBatchCard(Batch batch,
                                                  LocalDate today,
                                                  Map<Long, Long> batchEnrollmentCounts,
                                                  List<CandidateAttendance> attendanceRecords) {
        int totalDays = getInclusiveDays(batch.getStartDate(), batch.getEndDate());
        int completedDays = getCompletedDays(batch.getStartDate(), batch.getEndDate(), today);
        int progressPercentage = totalDays == 0 ? 0 : Math.min(100, (completedDays * 100) / totalDays);
        int enrolledCandidatesCount = batchEnrollmentCounts.getOrDefault(batch.getId(), 0L).intValue();
        List<CandidateAttendance> batchAttendanceRecords = attendanceRecords.stream()
                .filter(attendance -> batch.getId().equals(attendance.getBatch().getId()))
                .toList();
        int averageAttendancePercentage = calculateAverageAttendancePercentage(batchAttendanceRecords);
        boolean attendanceMarkedToday = isAttendanceMarkedToday(batchAttendanceRecords, today, enrolledCandidatesCount);

        return TrainerBatchCardResponse.builder()
                .batchId(batch.getId())
                .batchName(batch.getBatchName())
                .courseName(batch.getCourse().getCourseName())
                .enrolledCandidatesCount(enrolledCandidatesCount)
                .startDate(batch.getStartDate())
                .endDate(batch.getEndDate())
                .classProgressCompleted(completedDays)
                .classProgressTotal(totalDays)
                .classProgressPercentage(progressPercentage)
                .averageAttendancePercentage(averageAttendancePercentage)
                .attendanceMarkedToday(attendanceMarkedToday)
                .capacity(batch.getCapacity())
                .build();
    }

    private List<TrainerBatchLowAttendanceAlertResponse> buildLowAttendanceAlerts(List<Batch> activeBatches,
                                                                             List<Admission> admissions,
                                                                             List<CandidateAttendance> attendanceRecords) {
        List<TrainerBatchLowAttendanceAlertResponse> alerts = new ArrayList<>();

        for (Batch batch : activeBatches) {
            List<Admission> batchAdmissions = admissions.stream()
                    .filter(admission -> batch.getId().equals(admission.getBatch().getId()))
                    .toList();

            for (Admission batchAdmission : batchAdmissions) {
                int candidateAttendancePercentage = calculateCandidateAttendancePercentage(
                        batchAdmission.getCandidate(),
                        batch,
                        attendanceRecords
                );

                if (candidateAttendancePercentage > 0 && candidateAttendancePercentage < 75) {
                    alerts.add(TrainerBatchLowAttendanceAlertResponse.builder()
                            .candidateId(batchAdmission.getCandidate().getId())
                            .candidateName(batchAdmission.getCandidate().getFullName())
                            .batchId(batch.getId())
                            .batchName(batch.getBatchName())
                            .attendancePercentage(candidateAttendancePercentage)
                            .build());
                }
            }
        }

        return alerts.stream()
                .sorted((first, second) -> Integer.compare(first.getAttendancePercentage(), second.getAttendancePercentage()))
                .toList();
    }

    private int calculateAverageAttendancePercentage(List<CandidateAttendance> attendanceRecords) {
        if (attendanceRecords.isEmpty()) {
            return 0;
        }

        long presentCount = attendanceRecords.stream()
                .filter(attendance -> attendance.getAttendanceStatus() == CandidateAttendanceStatus.PRESENT)
                .count();

        return (int) Math.round((presentCount * 100.0) / attendanceRecords.size());
    }

    private boolean isAttendanceMarkedToday(List<CandidateAttendance> attendanceRecords, LocalDate today, int enrolledCandidatesCount) {
        if (enrolledCandidatesCount <= 0) {
            return false;
        }

        long todayMarkedCount = attendanceRecords.stream()
                .filter(attendance -> today.equals(attendance.getAttendanceDate()))
                .count();

        return todayMarkedCount >= enrolledCandidatesCount;
    }

    private int calculateCandidateAttendancePercentage(ApplicationForm candidate,
                                                       Batch batch,
                                                       List<CandidateAttendance> attendanceRecords) {
        List<CandidateAttendance> candidateRecords = attendanceRecords.stream()
                .filter(attendance -> attendance.getCandidate() != null)
                .filter(attendance -> attendance.getBatch() != null)
                .filter(attendance -> candidate.getId().equals(attendance.getCandidate().getId()))
                .filter(attendance -> batch.getId().equals(attendance.getBatch().getId()))
                .toList();

        if (candidateRecords.isEmpty()) {
            return 0;
        }

        long presentCount = candidateRecords.stream()
                .filter(attendance -> attendance.getAttendanceStatus() == CandidateAttendanceStatus.PRESENT)
                .count();

        return (int) Math.round((presentCount * 100.0) / candidateRecords.size());
    }

    private int getInclusiveDays(LocalDate startDate, LocalDate endDate) {
        return Math.max(0, (int) ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    private int getCompletedDays(LocalDate startDate, LocalDate endDate, LocalDate today) {
        LocalDate effectiveEndDate = today.isBefore(endDate) ? today : endDate;
        if (effectiveEndDate.isBefore(startDate)) {
            return 0;
        }
        return Math.max(0, (int) ChronoUnit.DAYS.between(startDate, effectiveEndDate) + 1);
    }
}
