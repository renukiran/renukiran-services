package com.renukiran.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record SignUpRequest(
        @NotBlank
        @Size(min = 4, max = 20)
        String userName,

        @NotBlank
       /* @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$",
                message = "Password must contain uppercase and special character"
        )*/
        String password,

        @Email
        @NotBlank
        String email,

        @Pattern(regexp = "^\\+91[0-9]{10}$")
        String phone,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotEmpty
        Set<String> skills,

        @NotEmpty
        String userType
) {
}
