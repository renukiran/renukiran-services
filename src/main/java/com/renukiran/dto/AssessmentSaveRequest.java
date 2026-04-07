package com.renukiran.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AssessmentSaveRequest {

    @Valid
    @NotEmpty(message = "Assessment entries cannot be empty")
    private List<AssessmentEntryRequest> entries;
}
