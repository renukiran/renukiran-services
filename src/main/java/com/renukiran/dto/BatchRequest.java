package com.renukiran.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BatchRequest{

    @NotBlank
    @Size(max = 100)
    private String batchName;

    private String timing;

    @NotNull
    private Long courseId;

    @NotNull
    private Long trainerId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Min(1)
    @Max(100)
    private Integer capacity;
}
