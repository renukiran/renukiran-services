package com.renukiran.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationResult {
    private String username;

    private String status;

    private String message;
}
