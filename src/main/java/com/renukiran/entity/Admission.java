package com.renukiran.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Table(name = "admissions",
       uniqueConstraints = @UniqueConstraint(name = "uk_candidate_batch", columnNames = {"candidate_id", "batch_id"}))
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admission {

    @Column(nullable = false, unique = true)
    @Id
    private String admissionNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonBackReference
    private ApplicationForm candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Batch batch;

}
