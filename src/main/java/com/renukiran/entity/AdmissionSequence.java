package com.renukiran.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Admission_Sequence")
@Getter
@Setter
public class AdmissionSequence {
    @Id
    private Integer seqYear;

    private Integer lastNumber;
}
