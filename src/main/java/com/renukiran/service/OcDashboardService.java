package com.renukiran.service;

import com.renukiran.dto.OcDashboardMetricResponse;
import com.renukiran.dto.OcDashboardResponse;
import com.renukiran.dto.OcRecentApplicationResponse;
import com.renukiran.dto.OcUpcomingFollowUpResponse;
import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.CandidateFollowUp;
import com.renukiran.enums.ApplicationStatus;
import com.renukiran.repository.AdmissionRepository;
import com.renukiran.repository.ApplicationFormRepository;
import com.renukiran.repository.CandidateFollowUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcDashboardService {

    private final ApplicationFormRepository applicationFormRepository;
    private final AdmissionRepository admissionRepository;
    private final CandidateFollowUpRepository candidateFollowUpRepository;

    @Transactional(readOnly = true)
    public OcDashboardResponse getDashboard(String coordinatorName) {
        LocalDate today = LocalDate.now();
        List<ApplicationForm> applications = applicationFormRepository.findAll();
        List<Admission> admissions = admissionRepository.findAll();
        List<CandidateFollowUp> followUps = candidateFollowUpRepository.findAll();

        Set<Long> assignedCandidateIds = admissions.stream()
                .filter(admission -> admission.getCandidate() != null)
                .map(admission -> admission.getCandidate().getId())
                .collect(Collectors.toSet());

        int newApplicationsCount = (int) applications.stream()
                .filter(application -> resolveStatus(application) == ApplicationStatus.NEW)
                .count();

        int underReviewCount = (int) applications.stream()
                .filter(application -> resolveStatus(application) == ApplicationStatus.UNDER_REVIEW)
                .count();

        int pendingPlacementCount = (int) applications.stream()
                .filter(application -> resolveStatus(application) == ApplicationStatus.PENDING_PLACEMENT)
                .count();

        List<OcRecentApplicationResponse> recentApplications = applications.stream()
                .sorted(Comparator.comparing(this::resolveCreatedDate, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(ApplicationForm::getId, Comparator.reverseOrder()))
                .limit(5)
                .map(application -> OcRecentApplicationResponse.builder()
                        .candidateId(application.getId())
                        .candidateName(application.getFullName())
                        .courseName(resolveCourseName(application))
                        .status(formatStatus(resolveStatus(application)))
                        .appliedDate(resolveCreatedDate(application))
                        .build())
                .toList();

        List<OcUpcomingFollowUpResponse> upcomingFollowUps = followUps.stream()
                .filter(followUp -> !Boolean.TRUE.equals(followUp.getCompleted()))
                .sorted(Comparator.comparing(CandidateFollowUp::getDueDate))
                .limit(5)
                .map(followUp -> OcUpcomingFollowUpResponse.builder()
                        .followUpId(followUp.getId())
                        .candidateId(followUp.getCandidate().getId())
                        .candidateName(followUp.getCandidate().getFullName())
                        .followUpType(followUp.getFollowUpType())
                        .dueDate(followUp.getDueDate())
                        .dueLabel(buildDueLabel(followUp.getDueDate(), today))
                        .build())
                .toList();

        return OcDashboardResponse.builder()
                .welcomeMessage("Welcome back, " + (coordinatorName == null || coordinatorName.isBlank() ? "Coordinator" : coordinatorName) + ". Here's your overview.")
                .generatedOn(today)
                .newApplications(OcDashboardMetricResponse.builder().label("New Applications").value(newApplicationsCount).build())
                .underReview(OcDashboardMetricResponse.builder().label("Under Review").value(underReviewCount).build())
                .assignedToBatch(OcDashboardMetricResponse.builder().label("Assigned to Batch").value(assignedCandidateIds.size()).build())
                .pendingPlacement(OcDashboardMetricResponse.builder().label("Pending Placement").value(pendingPlacementCount).build())
                .recentApplications(recentApplications)
                .upcomingFollowUps(upcomingFollowUps)
                .build();
    }

    private ApplicationStatus resolveStatus(ApplicationForm application) {
        return application.getApplicationStatus() != null ? application.getApplicationStatus() : ApplicationStatus.NEW;
    }

    private LocalDate resolveCreatedDate(ApplicationForm application) {
        return application.getCreatedDate();
    }

    private String resolveCourseName(ApplicationForm application) {
        if (application.getAppliedCourse() != null) {
            return application.getAppliedCourse().getCourseName();
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

    private String formatStatus(ApplicationStatus status) {
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

    private String buildDueLabel(LocalDate dueDate, LocalDate today) {
        long days = ChronoUnit.DAYS.between(today, dueDate);
        if (days == 0) {
            return "Today";
        }
        if (days == 1) {
            return "Tomorrow";
        }
        if (days < 0) {
            return "Overdue";
        }
        return "In " + days + " days";
    }
}
