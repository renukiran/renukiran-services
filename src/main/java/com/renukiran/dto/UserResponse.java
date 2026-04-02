package com.renukiran.dto;

import com.renukiran.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private Set<String> skills;
    private String userType;
    private boolean active;

    public static UserResponse from(Users u) {
        return UserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .email(u.getEmail())
                .phone(u.getPhone())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .skills(u.getSkills())
                .userType(u.getUserType())
                .active(u.isActive())
                .build();
    }
}
