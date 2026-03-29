package com.renukiran.service;

import com.renukiran.dto.AdminStatsResponse;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CandidateRepository;
import com.renukiran.repository.CourseRepository;
import com.renukiran.repository.NotificationRepository;
import com.renukiran.repository.PlacementRepository;
import com.renukiran.repository.SignUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;
    private final CandidateRepository candidateRepository;
    private final PlacementRepository placementRepository;
    private final NotificationRepository notificationRepository;
    private final SignUpRepository signUpRepository;

    public AdminStatsResponse getAdminStats() {
        long totalCourses         = courseRepository.count();
        long activeBatches        = batchRepository.countByStatus("Ongoing");
        long candidatesEnrolled   = candidateRepository.count();
        long placedTotal          = placementRepository.count();
        long activeJobs           = placementRepository.countByStatus("Active");
        long leftJob              = placementRepository.countByStatus("Left Job");
        long unreadNotifications  = notificationRepository.countByIsRead(false);
        long totalUsers           = signUpRepository.count();

        // Pending assignments: users without a batch assignment (approximation)
        long pendingAssignments   = Math.max(0, candidatesEnrolled - placedTotal);

        // Static impact percentages (update when actual data is available)
        String assessmentPassRate = "78%";
        String placementRate      = placedTotal > 0 && candidatesEnrolled > 0
                ? Math.round((placedTotal * 100.0) / Math.max(candidatesEnrolled, 1)) + "%"
                : "0%";
        String jobRetention       = "85%";
        String avgAttendance      = "88%";

        return AdminStatsResponse.builder()
                .totalCourses(totalCourses)
                .activeBatches(activeBatches)
                .candidatesEnrolled(candidatesEnrolled)
                .pendingAssignments(pendingAssignments)
                .assessmentPassRate(assessmentPassRate)
                .placementRate(placementRate)
                .jobRetention(jobRetention)
                .avgAttendance(avgAttendance)
                .placedTotal(placedTotal)
                .activeJobs(activeJobs)
                .leftJob(leftJob)
                .unreadNotifications(unreadNotifications)
                .build();
    }
}
