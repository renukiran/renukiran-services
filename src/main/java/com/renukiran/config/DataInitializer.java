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
import java.util.Set;

/**
 * Seeds a temporary admin account and sample data into the in-memory H2 database
 * on every startup.
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
            // ── Admin User ────────────────────────────────────────────────────
            final String tempUsername = "TempAdmin";
            if (!signUpRepository.existsByUsername(tempUsername)) {
                signUpRepository.save(Users.builder()
                        .username(tempUsername)
                        .password("Admin@1234")
                        .email("tempAdmin@renukiran.com")
                        .phone("+910000000000")
                        .firstName("Temp")
                        .lastName("Admin")
                        .skills(Set.of("Administration"))
                        .userType("ADMIN")
                        .build());
                log.info("[DataInitializer] Temporary admin '{}' seeded.", tempUsername);
            }

                        final String coordinatorUsername = "TempCoordinator";
                        if (!signUpRepository.existsByUsername(coordinatorUsername)) {
                                signUpRepository.save(Users.builder()
                                                .username(coordinatorUsername)
                                                .password("Coordinator@1234")
                                                .email("tempCoordinator@renukiran.com")
                                                .phone("+910000000001")
                                                .firstName("Temp")
                                                .lastName("Coordinator")
                                                .skills(Set.of("Coordination"))
                                                .userType("COORDINATOR")
                                                .build());
                                log.info("[DataInitializer] Temporary coordinator '{}' seeded.", coordinatorUsername);
                        }

                        final String trainerUsername = "TempTrainer";
                        Users trainerUser = signUpRepository.findByUsername(trainerUsername)
                                        .orElseGet(() -> {
                                                Users savedUser = signUpRepository.save(Users.builder()
                                                                .username(trainerUsername)
                                                                .password("Trainer@1234")
                                                                .email("tempTrainer@renukiran.com")
                                                                .phone("+910000000002")
                                                                .firstName("Temp")
                                                                .lastName("Trainer")
                                                                .skills(Set.of("Training"))
                                                                .userType("TRAINER")
                                                                .build());
                                                log.info("[DataInitializer] Temporary trainer '{}' seeded.", trainerUsername);
                                                return savedUser;
                                        });

            // ── Courses ───────────────────────────────────────────────────────
            if (courseRepository.count() == 0) {
                List<Course> courses = List.of(
                    Course.builder().courseName("Stitching Basic").instructor("Suman Kumar").durationMonths(3).maxBatchSize(20).category("Vocational").status("Active").build(),
                    Course.builder().courseName("Computer Fundamentals").instructor("Raj Patel").durationMonths(3).maxBatchSize(20).category("Technical").status("Active").build(),
                    Course.builder().courseName("Beauty Basic").instructor("Asha Mehra").durationMonths(3).maxBatchSize(20).category("Vocational").status("Active").build(),
                    Course.builder().courseName("Food Enterprise").instructor("Suman Kumar").durationMonths(3).maxBatchSize(20).category("Entrepreneurship").status("Active").build()
                );
                courseRepository.saveAll(courses);
                log.info("[DataInitializer] {} courses seeded.", courses.size());
            }

            // ── Trainers ──────────────────────────────────────────────────────
            if (trainerRepository.count() == 0) {
                                trainerRepository.saveAll(List.of(
                                        buildTrainer("Suman Kumar", null),
                                        buildTrainer("Raj Patel", null),
                                        buildTrainer("Asha Mehra", trainerUser.getId()),
                                        buildTrainer("Priya T.", null)
                                ));
                log.info("[DataInitializer] Trainers seeded.");
            }

            // ── Batches ───────────────────────────────────────────────────────
            if (batchRepository.count() == 0 && courseRepository.count() > 0 && trainerRepository.count() > 0) {
                List<Course> courses = courseRepository.findAll();
                List<Trainer> trainers = trainerRepository.findAll();
                List<Batch> batches = List.of(
                    Batch.builder().batchName("Stitching Basic Jan-Mar 2026").course(courses.get(0))
                            .trainer(trainers.get(0)).startDate(LocalDate.of(2026,1,1))
                            .endDate(LocalDate.of(2026,3,31)).capacity(20).build(),
                    Batch.builder().batchName("Computer Fund Jan-Apr 2026").course(courses.get(1))
                            .trainer(trainers.get(1)).startDate(LocalDate.of(2026,1,15))
                            .endDate(LocalDate.of(2026,4,15)).capacity(20).build(),
                    Batch.builder().batchName("Beauty Basic Apr-Jul 2026").course(courses.get(2))
                            .trainer(trainers.get(2)).startDate(LocalDate.of(2026,4,1))
                            .endDate(LocalDate.of(2026,7,31)).capacity(20).build()
                );
                batchRepository.saveAll(batches);
                log.info("[DataInitializer] {} batches seeded.", batches.size());
            }

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

        private Trainer buildTrainer(String name, Long userId) {
                Trainer trainer = new Trainer();
                trainer.setName(name);
                trainer.setUserId(userId);
                return trainer;
        }
}
