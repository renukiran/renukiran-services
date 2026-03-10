package com.renukiran.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BulkRegistrationRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    private String phone;

    private String firstName;

    private String lastName;

    private String dateOfBirth;

    private String address;

    private String preferredCourseCode;

    private String userType;
}
