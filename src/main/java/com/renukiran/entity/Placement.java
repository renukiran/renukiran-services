package com.renukiran.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "placements")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Placement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;        // candidate name

    private String employer;
    private String role;
    private Integer salary;
    private String placedDate;

    @Column(nullable = false)
    @Builder.Default
    private String status = "Active";   // Active | Left Job | Unknown

    private String course;
    private String batch;
    private String assessment;  // e.g. "86.1%"

    @OneToMany(mappedBy = "placement", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<PlacementFollowup> followups = new ArrayList<>();
}
