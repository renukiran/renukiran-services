package com.renukiran.config;

import com.renukiran.entity.*;
import com.renukiran.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

            // ── Batches ───────────────────────────────────────────────────────
            if (batchRepository.count() == 0) {
                List<Batch> batches = List.of(
                    Batch.builder().batchCode("B1").course("Stitching Basic").trainer("Suman Kumar")
                            .location("Main Hall").dates("Jan – Mar 2026").startDate("2026-01-01")
                            .endDate("2026-03-31").enrolled(18).max(20).status("Ongoing").build(),
                    Batch.builder().batchCode("B2").course("Computer Fundamentals").trainer("Raj Patel")
                            .location("Lab Room").dates("Jan – Apr 2026").startDate("2026-01-15")
                            .endDate("2026-04-15").enrolled(20).max(20).status("Ongoing").build(),
                    Batch.builder().batchCode("B3").course("Beauty Basic").trainer("Asha Mehra")
                            .location("Room 3").dates("Nov 2025 – Jan 2026").startDate("2025-11-01")
                            .endDate("2026-01-31").enrolled(15).max(20).status("Completed").build(),
                    Batch.builder().batchCode("B4").course("Stitching Basic").trainer("Priya T.")
                            .location("Main Hall").dates("Dec 2025 – Feb 2026").startDate("2025-12-01")
                            .endDate("2026-02-28").enrolled(12).max(20).status("Completed").build(),
                    Batch.builder().batchCode("B5").course("Food Enterprise").trainer("Suman Kumar")
                            .location("Kitchen Lab").dates("Apr – Jun 2026").startDate("2026-04-01")
                            .endDate("2026-06-30").enrolled(0).max(20).status("Upcoming").build()
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
}
