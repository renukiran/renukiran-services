package com.renukiran.dto;

import com.renukiran.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserRequest {

    @NotBlank
    private String fullName;

    @Email
    private String email;

    private String phone;

    private String password;

    @NotNull
    private RoleType role;

    private List<Long> courseIds;
}
