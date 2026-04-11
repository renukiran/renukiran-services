package com.renukiran.service;

import com.renukiran.dto.AdminAttendanceAlertResponse;
import com.renukiran.dto.AdminBatchCapacityResponse;
import com.renukiran.dto.AdminDashboardMetricResponse;
import com.renukiran.dto.AdminDashboardResponse;
import com.renukiran.dto.AdminRecentApplicationResponse;
import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Batch;
import com.renukiran.entity.CandidateAssessment;
import com.renukiran.entity.CandidateAttendance;
import com.renukiran.entity.CandidateFollowUp;
import com.renukiran.entity.Course;
import com.renukiran.enums.AdmissionStatus;
import com.renukiran.enums.CandidateAttendanceStatus;
import com.renukiran.repository.AdmissionRepository;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CandidateAssessmentRepository;
import com.renukiran.repository.CandidateAttendanceRepository;
import com.renukiran.repository.CandidateFollowUpRepository;
import com.renukiran.repository.CourseRepository;
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
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private static final int DEFAULT_PASS_THRESHOLD = 50;
    private static final int ATTENDANCE_ALERT_THRESHOLD = 75;

    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;
    private final AdmissionRepository admissionRepository;
    private final ApplicationFormService applicationFormService;
    private final CandidateAssessmentRepository candidateAssessmentRepository;
    private final CandidateAttendanceRepository candidateAttendanceRepository;
    private final CandidateFollowUpRepository candidateFollowUpRepository;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard(String adminName) {
        LocalDate today = LocalDate.now();
        List<Course> courses = courseRepository.findAll();
        List<Batch> batches = batchRepository.findAll();
        List<Admission> admissions = admissionRepository.findAll();
        List<ApplicationForm> applications = applicationFormService.listAll();
        List<CandidateAssessment> assessments = candidateAssessmentRepository.findAll();
        List<CandidateAttendance> attendanceRecords = candidateAttendanceRepository.findAll();
        List<CandidateFollowUp> followUps = candidateFollowUpRepository.findAll();

        List<Batch> activeBatches = batches.stream()
                .filter(batch -> !batch.getStartDate().isAfter(today) && !batch.getEndDate().isBefore(today))
                .toList();

        Map<Long, Integer> enrolledCountByBatchId = buildEnrolledCountByBatchId(admissions);

        int totalCourses = courses.size();
        int activeBatchesCount = activeBatches.size();
        int candidatesEnrolled = admissions.size();
        int pendingAssignments = (int) applications.stream()
                .filter(application -> application.getAdmissions() == null || application.getAdmissions().isEmpty())
                .count();

        int assessedCount = 0;
        int passCount = 0;
        double totalAssessmentScore = 0.0;
        for (CandidateAssessment assessment : assessments) {
            Double finalPercentage = calculateFinalPercentage(assessment);
            if (finalPercentage == null) {
                continue;
            }
            assessedCount++;
            totalAssessmentScore += finalPercentage;
            if (finalPercentage >= resolvePassThreshold(assessment.getBatch().getCourse())) {
                passCount++;
            }
        }

        AtomicLong totalPlacementCandidates = new AtomicLong();
        applications.forEach(applicationForm -> {

            applicationForm.getAdmissions().forEach(admission -> {
                if (admission.getStatus() != null && admission.getStatus() == AdmissionStatus.TRAINING_COMPLETED ) {
                    totalPlacementCandidates.getAndIncrement();
                }
            });
        });

        AtomicLong placedCandidates = new AtomicLong();
        applications.forEach(applicationForm -> {

            applicationForm.getAdmissions().forEach(admission -> {
                if (admission.getStatus() != null && admission.getStatus() == AdmissionStatus.PLACED ) {
                    placedCandidates.getAndIncrement();
                }
            });
        });


        List<Course> coursesWithRetentionRate = courses.stream()
                .filter(course -> course.getRetentionRate() != null)
                .toList();

        long totalAttendanceRows = attendanceRecords.size();
        long presentAttendanceRows = attendanceRecords.stream()
                .filter(attendance -> attendance.getAttendanceStatus() == CandidateAttendanceStatus.PRESENT)
                .count();

        List<AdminRecentApplicationResponse> recentApplications = applications.stream()
                .sorted(Comparator.comparing(this::resolveCreatedDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ApplicationForm::getId, Comparator.reverseOrder()))
                .limit(5)
                .map(application -> AdminRecentApplicationResponse.builder()
                        .candidateId(application.getId())
                        .candidateName(application.getFullName())
                        .courseName(resolveCourseName(application))
                        .status(formatStatus(resolveStatus(application)))
                        .appliedDate(resolveCreatedDate(application))
                        .build())
                .toList();

        List<AdminBatchCapacityResponse> batchCapacityOverview = activeBatches.stream()
                .sorted(Comparator.comparing((Batch batch) -> calculateOccupancyPercentage(batch, enrolledCountByBatchId)).reversed()
                        .thenComparing(Batch::getId))
                .limit(3)
                .map(batch -> AdminBatchCapacityResponse.builder()
                        .batchId(batch.getId())
                        .courseName(batch.getCourse().getCourseName())
                        .batchName(batch.getBatchName())
                        .enrolledCount(enrolledCountByBatchId.getOrDefault(batch.getId(), 0))
                        .capacity(batch.getCapacity())
                        .occupancyPercentage(calculateOccupancyPercentage(batch, enrolledCountByBatchId))
                        .batchStatus(resolveBatchStatus(batch, today))
                        .build())
                .toList();

        List<AdminAttendanceAlertResponse> attendanceAlerts = buildAttendanceAlerts(activeBatches, admissions, attendanceRecords);

        return AdminDashboardResponse.builder()
                .welcomeMessage("Welcome back, " + (adminName == null || adminName.isBlank() ? "Admin" : adminName) + ". Here's your operational overview.")
                .generatedOn(today)
                .totalCourses(buildMetric("Total Courses", String.valueOf(totalCourses), null))
                .activeBatches(buildMetric("Active Batches", String.valueOf(activeBatchesCount), activeBatchesCount == 0 ? null : "Across " + countDistinctCourses(activeBatches) + " courses"))
                .candidatesEnrolled(buildMetric("Candidates Enrolled", String.valueOf(candidatesEnrolled), null))
                .pendingAssignments(buildMetric("Pending Assignments", String.valueOf(pendingAssignments), pendingAssignments > 0 ? "Needs attention" : "All clear"))
                .assessmentPassRate(buildMetric("Assessment Pass Rate", formatPercentage(calculatePercentage(passCount, assessedCount)), assessedCount > 0 ? "From assessed candidates" : null))
                .placementRate(buildMetric("Placement Rate", formatPercentage(calculatePercentage((int) placedCandidates.get(), (int) totalPlacementCandidates.get())), totalPlacementCandidates.get() > 0 ? null : "No placement data yet"))
                .jobRetention(buildMetric("Job Retention", formatPercentage(calculateAverageRetentionRate(coursesWithRetentionRate)), coursesWithRetentionRate.isEmpty() ? "No course retention configured yet" : "Course configured"))
                .averageAttendance(buildMetric("Avg Attendance", formatPercentage(calculatePercentage((int) presentAttendanceRows, (int) totalAttendanceRows)), totalAttendanceRows > 0 ? null : "No attendance marked yet"))
                .recentApplications(recentApplications)
                .batchCapacityOverview(batchCapacityOverview)
                .attendanceAlerts(attendanceAlerts)
                .build();
    }

    private AdmissionStatus resolveStatus(ApplicationForm application) {
        return application.getAdmissions() != null  && !application.getAdmissions().isEmpty() ?  application.getAdmissions().get(application.getAdmissions().size()-1).getStatus()  : AdmissionStatus.NEW;
    }

    private Map<Long, Integer> buildEnrolledCountByBatchId(List<Admission> admissions) {
        Map<Long, Integer> enrolledCountByBatchId = new HashMap<>();
        for (Admission admission : admissions) {
            if (admission.getBatch() == null) {
                continue;
            }
            enrolledCountByBatchId.merge(admission.getBatch().getId(), 1, Integer::sum);
        }
        return enrolledCountByBatchId;
    }

    private List<AdminAttendanceAlertResponse> buildAttendanceAlerts(List<Batch> activeBatches,
                                                                     List<Admission> admissions,
                                                                     List<CandidateAttendance> attendanceRecords) {
        Set<Long> activeBatchIds = activeBatches.stream().map(Batch::getId).collect(java.util.stream.Collectors.toSet());
        List<AdminAttendanceAlertResponse> alerts = new ArrayList<>();

        for (Admission admission : admissions) {
            if (admission.getBatch() == null || admission.getCandidate() == null) {
                continue;
            }
            if (!activeBatchIds.contains(admission.getBatch().getId())) {
                continue;
            }

            List<CandidateAttendance> candidateAttendance = attendanceRecords.stream()
                    .filter(attendance -> attendance.getBatch() != null)
                    .filter(attendance -> attendance.getCandidate() != null)
                    .filter(attendance -> admission.getBatch().getId().equals(attendance.getBatch().getId()))
                    .filter(attendance -> admission.getCandidate().getId().equals(attendance.getCandidate().getId()))
                    .toList();

            if (candidateAttendance.isEmpty()) {
                continue;
            }

            long presentCount = candidateAttendance.stream()
                    .filter(attendance -> attendance.getAttendanceStatus() == CandidateAttendanceStatus.PRESENT)
                    .count();
            int attendancePercentage = calculatePercentage((int) presentCount, candidateAttendance.size());
            if (attendancePercentage < ATTENDANCE_ALERT_THRESHOLD) {
                alerts.add(AdminAttendanceAlertResponse.builder()
                        .candidateId(admission.getCandidate().getId())
                        .candidateName(admission.getCandidate().getFullName())
                        .batchId(admission.getBatch().getId())
                        .courseName(admission.getBatch().getCourse().getCourseName())
                        .batchName(admission.getBatch().getBatchName())
                        .attendancePercentage(attendancePercentage)
                        .build());
            }
        }

        return alerts.stream()
                .sorted(Comparator.comparing(AdminAttendanceAlertResponse::getAttendancePercentage)
                        .thenComparing(AdminAttendanceAlertResponse::getCandidateName, String.CASE_INSENSITIVE_ORDER))
                .limit(5)
                .toList();
    }

    private Integer calculateOccupancyPercentage(Batch batch, Map<Long, Integer> enrolledCountByBatchId) {
        if (batch.getCapacity() == null || batch.getCapacity() <= 0) {
            return 0;
        }
        int enrolledCount = enrolledCountByBatchId.getOrDefault(batch.getId(), 0);
        return Math.min(100, (int) Math.round((enrolledCount * 100.0) / batch.getCapacity()));
    }

    private String resolveBatchStatus(Batch batch, LocalDate today) {
        if (batch.getEndDate().isBefore(today)) {
            return "Completed";
        }
        if (batch.getStartDate().isAfter(today)) {
            return "Upcoming";
        }
        return "Ongoing";
    }



    private LocalDate resolveCreatedDate(ApplicationForm application) {
        return application.getCreatedDate();
    }

    private String resolveCourseName(ApplicationForm application) {
        if (application.getAdmissions() != null && !application.getAdmissions().isEmpty()
                && application.getAdmissions().get(0).getBatch() != null
                && application.getAdmissions().get(0).getBatch().getCourse() != null) {
            return application.getAdmissions().get(0).getBatch().getCourse().getCourseName();
        }
        if (application.getPreferredExperienceTrack() != null) {
            return formatEnumName(application.getPreferredExperienceTrack().name());
        }
        if (application.getAdmissions() != null && !application.getAdmissions().isEmpty()) {
            Admission admission = application.getAdmissions().iterator().next();
            if (admission.getBatch() != null && admission.getBatch().getCourse() != null) {
                return admission.getBatch().getCourse().getCourseName();
            }
        }
        return "Not assigned";
    }

    private Double calculateFinalPercentage(CandidateAssessment assessment) {
        if (assessment == null
                || assessment.getBatch() == null
                || assessment.getBatch().getCourse() == null
                || assessment.getMcqScore() == null
                || assessment.getPracticalScore() == null
                || assessment.getCaseStudyScore() == null) {
            return null;
        }

        int mcqWeight = parseWeight(assessment.getBatch().getCourse().getMcqAssessment());
        int practicalWeight = parseWeight(assessment.getBatch().getCourse().getPracticalAssessment());
        int caseStudyWeight = parseWeight(assessment.getBatch().getCourse().getCaseStudyAssessment());
        int totalWeight = mcqWeight + practicalWeight + caseStudyWeight;
        if (totalWeight <= 0) {
            return null;
        }

        double weightedScore = (assessment.getMcqScore() * mcqWeight)
                + (assessment.getPracticalScore() * practicalWeight)
                + (assessment.getCaseStudyScore() * caseStudyWeight);

        return roundOneDecimal(weightedScore / totalWeight);
    }

    private int resolvePassThreshold(Course course) {
        return course.getPassThreshold() != null ? course.getPassThreshold() : DEFAULT_PASS_THRESHOLD;
    }

    private int calculateAverageRetentionRate(List<Course> courses) {
        if (courses.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (Course course : courses) {
            total += course.getRetentionRate();
        }
        return (int) Math.round(total / courses.size());
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

    private int countDistinctCourses(List<Batch> batches) {
        Set<Long> courseIds = new HashSet<>();
        for (Batch batch : batches) {
            if (batch.getCourse() != null) {
                courseIds.add(batch.getCourse().getCourseId());
            }
        }
        return courseIds.size();
    }

    private AdminDashboardMetricResponse buildMetric(String label, String value, String helperText) {
        return AdminDashboardMetricResponse.builder()
                .label(label)
                .value(value)
                .helperText(helperText)
                .build();
    }

    private int calculatePercentage(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0;
        }
        return (int) Math.round((numerator * 100.0) / denominator);
    }

    private String formatPercentage(int percentage) {
        return percentage + "%";
    }

    private double roundOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String formatStatus(AdmissionStatus status) {
        return formatEnumName(status.name());
    }

    private String formatEnumName(String value) {
        String[] parts = value.replace('_', ' ').toLowerCase().split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }
        return builder.toString();
    }
}
