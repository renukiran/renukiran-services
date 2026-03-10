package com.renukiran.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkRegistrationResponse {
    private int total;

    private int registered;

    private int failed;

    private List<UserRegistrationResult> results;
}
