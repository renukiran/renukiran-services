package com.renukiran.config;

import com.renukiran.entity.*;
import com.renukiran.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Seeds local development accounts and sample data into the local H2 database.
 * Records are inserted only when missing so the file-backed local profile keeps
 * user-created changes across restarts.
 *
 * ⚠️  FOR DEVELOPMENT/TESTING ONLY. Remove or replace before moving to production.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final SignUpRepository signUpRepository;
    private final CourseRepository courseRepository;
    private final TrainerRepository trainerRepository;
    private final BatchRepository batchRepository;
    private final PlacementRepository placementRepository;
    private final NotificationRepository notificationRepository;

    @Bean
    CommandLineRunner seedTempAdmin() {
        return args -> {
            // ── Users ─────────────────────────────────────────────────────────
            ensureUser("TempAdmin", "Admin@1234", "tempAdmin@renukiran.com", "+910000000000",
                    "Temp", "Admin", Set.of("Administration"), "ADMIN");
            ensureUser("TempCoordinator", "Coordinator@1234", "tempCoordinator@renukiran.com", "+910000000001",
                    "Temp", "Coordinator", Set.of("Coordination"), "COORDINATOR");
            Users trainerUser = ensureUser("TempTrainer", "Trainer@1234", "tempTrainer@renukiran.com", "+910000000002",
                    "Temp", "Trainer", Set.of("Training"), "TRAINER");
            Users sanjayTrainerUser = ensureUser("sanjaymalik264179", "Trainer@1234", "sanjay.malik.264179@rwf.org", "+919543210876",
                    "Sanjay", "Malik", Set.of("Beauty", "Stitching"), "TRAINER");

            // ── Courses ───────────────────────────────────────────────────────
            Course stitchingBasic = ensureCourse("Stitching Basic", "Suman Kumar", 3, 20, "Vocational",
                    null, "30", "50", "20", 50, "Active");
            Course computerFundamentals = ensureCourse("Computer Fundamentals", "Raj Patel", 3, 20, "Technical",
                    null, "40", "40", "20", 50, "Active");
            Course beautyBasic = ensureCourse("Beauty Basic", "Asha Mehra", 3, 20, "Vocational",
                    null, "30", "50", "20", 50, "Active");
            ensureCourse("Food Enterprise", "Suman Kumar", 3, 20, "Entrepreneurship",
                    null, "25", "55", "20", 50, "Active");
            Course beautyIntensive = ensureCourse("Beauty Intensive 264179", "Asha Mehra", 4, 18, "Beauty",
                    "Salon, customer service, and income planning for neighbourhood women.", "30", "50", "20", 50, "Active");

            // ── Trainers ──────────────────────────────────────────────────────
            Trainer sumanTrainer = ensureTrainer("Suman Kumar", null);
            Trainer rajTrainer = ensureTrainer("Raj Patel", null);
            Trainer ashaTrainer = ensureTrainer("Asha Mehra", trainerUser.getId());
            ensureTrainer("Priya T.", null);
            ensureTrainer("Sanjay Malik", sanjayTrainerUser.getId());

            // ── Batches ───────────────────────────────────────────────────────
            ensureBatch("Stitching Basic Jan-Mar 2026", stitchingBasic, sumanTrainer,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31), 20);
            ensureBatch("Computer Fund Jan-Apr 2026", computerFundamentals, rajTrainer,
                    LocalDate.of(2026, 1, 15), LocalDate.of(2026, 4, 15), 20);
            ensureBatch("Beauty Basic Apr-Jul 2026", beautyBasic, ashaTrainer,
                    LocalDate.of(2026, 4, 1), LocalDate.of(2026, 7, 31), 20);
            ensureBatch("Beauty Morning 264179", beautyIntensive, ashaTrainer,
                    LocalDate.of(2026, 4, 20), LocalDate.of(2026, 8, 20), 18);

            // ── Placements ────────────────────────────────────────────────────
            if (placementRepository.count() == 0) {
                Placement p1 = Placement.builder().name("Priya Sharma").employer("ABC Textiles Ltd")
                        .role("Tailor").salary(8000).placedDate("Feb 15, 2026").status("Active")
                        .course("Stitching Basic").batch("B1").assessment("86.1%").build();
                Placement p2 = Placement.builder().name("Meena Devi").employer("Lakshmi Garments")
                        .role("Machine Operator").salary(7500).placedDate("Jan 20, 2026").status("Active")
                        .course("Stitching Basic").batch("B1").assessment("79.4%").build();
                Placement p3 = Placement.builder().name("Geeta Kumari").employer("Ravi Data Services")
                        .role("Data Entry Operator").salary(10000).placedDate("Dec 10, 2025").status("Active")
                        .course("Computer Fundamentals").batch("B2").assessment("91.2%").build();
                Placement p4 = Placement.builder().name("Anjali Patel").employer("Shree Beauty Parlour")
                        .role("Beautician").salary(5000).placedDate("Nov 5, 2025").status("Left Job")
                        .course("Beauty Basic").batch("B3").assessment("73.8%").build();
                Placement p5 = Placement.builder().name("Rekha Yadav").employer("Bharat Fashions Pvt")
                        .role("Assistant Tailor").salary(12000).placedDate("Jan 8, 2026").status("Unknown")
                        .course("Stitching Basic").batch("B4").assessment("68.5%").build();

                placementRepository.saveAll(List.of(p1, p2, p3, p4, p5));

                // Add followups for p1
                List<PlacementFollowup> f1 = List.of(
                    PlacementFollowup.builder().placement(p1).label("1 Month").date("Mar 15, 2026")
                            .done(true).overdue(false).note("Doing well, learning new patterns")
                            .statusAtCheck("Active").salaryAtCheck(8000).build(),
                    PlacementFollowup.builder().placement(p1).label("3 Months").date("May 15, 2026")
                            .done(false).overdue(false).build(),
                    PlacementFollowup.builder().placement(p1).label("6 Months").date("Aug 15, 2026")
                            .done(false).overdue(false).build()
                );
                p1.getFollowups().addAll(f1);
                placementRepository.save(p1);

                log.info("[DataInitializer] 5 placements seeded.");
            }

            // ── Notifications ─────────────────────────────────────────────────
            if (notificationRepository.count() == 0) {
                List<Notification> notifications = List.of(
                    Notification.builder().message("New application submitted by Priya Sharma")
                            .isRead(false).createdAt(LocalDateTime.now().minusHours(2)).build(),
                    Notification.builder().message("Batch B5 (Food Enterprise) starts in 5 days")
                            .isRead(false).createdAt(LocalDateTime.now().minusDays(1)).build(),
                    Notification.builder().message("Placement offer received for Geeta Kumari")
                            .isRead(true).createdAt(LocalDateTime.now().minusDays(3)).build(),
                    Notification.builder().message("Anjali Patel follow-up is overdue")
                            .isRead(false).createdAt(LocalDateTime.now().minusDays(5)).build()
                );
                notificationRepository.saveAll(notifications);
                log.info("[DataInitializer] {} notifications seeded.", notifications.size());
            }
        };
    }

        private Users ensureUser(String username, String password, String email, String phone,
                                                         String firstName, String lastName, Set<String> skills, String userType) {
                return signUpRepository.findByUsername(username)
                                .orElseGet(() -> {
                                        Users savedUser = signUpRepository.save(Users.builder()
                                                        .username(username)
                                                        .password(password)
                                                        .email(email)
                                                        .phone(phone)
                                                        .firstName(firstName)
                                                        .lastName(lastName)
                                                        .skills(skills)
                                                        .userType(userType)
                                                        .build());
                                        log.info("[DataInitializer] User '{}' seeded.", username);
                                        return savedUser;
                                });
        }

        private Course ensureCourse(String courseName, String instructor, Integer durationMonths, Integer maxBatchSize,
                                                                String category, String description, String mcqAssessment,
                                                                String practicalAssessment, String caseStudyAssessment,
                                                                Integer passThreshold, String status) {
                return courseRepository.findFirstByCourseNameIgnoreCase(courseName)
                                .orElseGet(() -> {
                                        Course savedCourse = courseRepository.save(Course.builder()
                                                        .courseName(courseName)
                                                        .instructor(instructor)
                                                        .durationMonths(durationMonths)
                                                        .maxBatchSize(maxBatchSize)
                                                        .category(category)
                                                        .description(description)
                                                        .mcqAssessment(mcqAssessment)
                                                        .practicalAssessment(practicalAssessment)
                                                        .caseStudyAssessment(caseStudyAssessment)
                                                        .passThreshold(passThreshold)
                                                        .status(status)
                                                        .build());
                                        log.info("[DataInitializer] Course '{}' seeded.", courseName);
                                        return savedCourse;
                                });
        }

        private Trainer ensureTrainer(String name, Long userId) {
                return trainerRepository.findFirstByNameIgnoreCase(name)
                                .map(existingTrainer -> {
                                        if (!Objects.equals(existingTrainer.getUserId(), userId)) {
                                                existingTrainer.setUserId(userId);
                                                Trainer savedTrainer = trainerRepository.save(existingTrainer);
                                                log.info("[DataInitializer] Trainer '{}' linked to local user {}.", name, userId);
                                                return savedTrainer;
                                        }
                                        return existingTrainer;
                                })
                                .orElseGet(() -> {
                                        Trainer savedTrainer = trainerRepository.save(buildTrainer(name, userId));
                                        log.info("[DataInitializer] Trainer '{}' seeded.", name);
                                        return savedTrainer;
                                });
        }

        private Batch ensureBatch(String batchName, Course course, Trainer trainer,
                                                          LocalDate startDate, LocalDate endDate, Integer capacity) {
                return batchRepository.findByBatchName(batchName)
                                .map(existingBatch -> {
                                        boolean changed = false;

                                        if (!Objects.equals(existingBatch.getCourse().getCourseId(), course.getCourseId())) {
                                                existingBatch.setCourse(course);
                                                changed = true;
                                        }
                                        if (!Objects.equals(existingBatch.getTrainer().getTrainerId(), trainer.getTrainerId())) {
                                                existingBatch.setTrainer(trainer);
                                                changed = true;
                                        }
                                        if (!Objects.equals(existingBatch.getStartDate(), startDate)) {
                                                existingBatch.setStartDate(startDate);
                                                changed = true;
                                        }
                                        if (!Objects.equals(existingBatch.getEndDate(), endDate)) {
                                                existingBatch.setEndDate(endDate);
                                                changed = true;
                                        }
                                        if (!Objects.equals(existingBatch.getCapacity(), capacity)) {
                                                existingBatch.setCapacity(capacity);
                                                changed = true;
                                        }

                                        if (!changed) {
                                                return existingBatch;
                                        }

                                        Batch savedBatch = batchRepository.save(existingBatch);
                                        log.info("[DataInitializer] Batch '{}' refreshed.", batchName);
                                        return savedBatch;
                                })
                                .orElseGet(() -> {
                                        Batch savedBatch = batchRepository.save(Batch.builder()
                                                        .batchName(batchName)
                                                        .course(course)
                                                        .trainer(trainer)
                                                        .startDate(startDate)
                                                        .endDate(endDate)
                                                        .capacity(capacity)
                                                        .build());
                                        log.info("[DataInitializer] Batch '{}' seeded.", batchName);
                                        return savedBatch;
                                });
        }

        private Trainer buildTrainer(String name, Long userId) {
                Trainer trainer = new Trainer();
                trainer.setName(name);
                trainer.setUserId(userId);
                return trainer;
        }
}
