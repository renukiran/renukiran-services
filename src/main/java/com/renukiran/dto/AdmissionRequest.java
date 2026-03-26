package com.renukiran.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdmissionRequest {


    @NotNull(message = "Candidate ID is mandatory")
    private Long candidateId;

    @NotNull(message = "Course ID is mandatory")
    private Long batchId;

}

