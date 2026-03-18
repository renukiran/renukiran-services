package com.renukiran.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="CANDIDATES",
uniqueConstraints = {
        @UniqueConstraint( name = "uk_candidate_mobile",columnNames = "mobile"),
        @UniqueConstraint(name = "uk_candidate_name_dob_mobile", columnNames = {"name","dob","mobile"})
})
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long candidateId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private LocalDate dob;
    private String gender;
    private String guardianName;
    private String qualification;
    private String occupation;
    private String nationality;
    private String religion;
    private String skills;
    private String category;
    @Column(nullable = false)
    private String mobile;
    @Column(length = 255)
    private String address;
    private String referenceName;
    // optional: track all admissions
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    private List<Admission> admissions = new ArrayList<>();
}
