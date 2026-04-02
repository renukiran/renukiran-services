package com.renukiran.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "placement_followups")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlacementFollowup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private Placement placement;

    private String label;           // "1 Month", "3 Months", "6 Months"
    private String date;            // "Mar 15, 2026"
    private Boolean done;
    private Boolean overdue;
    private String note;
    private String statusAtCheck;
    private Integer salaryAtCheck;
}
