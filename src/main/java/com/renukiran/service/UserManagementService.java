package com.renukiran.service;

import java.util.*;
import java.util.stream.Collectors;

import com.renukiran.dto.UserRequest;
import com.renukiran.dto.UserResponse;
import com.renukiran.entity.Course;
import com.renukiran.entity.Role;
import com.renukiran.entity.Users;
import com.renukiran.enums.RoleType;
import com.renukiran.repository.*;
import org.springframework.stereotype.Service;

import com.renukiran.dto.UserManagementResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final SignUpRepository signUpRepository;
    private final RoleRepository roleRepo;
    private final CourseRepository courseRepo;

    public List<UserManagementResponse> getAllUsers() {
        return signUpRepository.findAll().stream()
                .map(user -> {
                    UserManagementResponse res = new UserManagementResponse();
                    res.setName(user.getUsername());
                    res.setEmail(user.getEmail());
                    res.setStatus(user.isActive() ? "ACTIVE" : "INACTIVE");
                    res.setRoles(Optional.ofNullable(user.getRoles()).orElse(Collections.emptySet()).stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet()));
                    return res;
                }).collect(Collectors.toList());
    }

    public UserResponse createUser(UserRequest dto) {

        if (signUpRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Set<Role> roles = dto.getRoles().stream()
                .map(roleType -> roleRepo.findByName(roleType)
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());

        Users user = Users.builder()
                .username(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .roles(roles)
                .build();

        if (dto.getRoles().contains(RoleType.TRAINER)) {

            List<Course> courses = courseRepo.findAllById(dto.getCourseIds());

            if (courses.size() != dto.getCourseIds().size()) {
                throw new RuntimeException("Invalid courseIds");
            }

            user.setCourses(new HashSet<>(courses));
        }

            signUpRepository.save(user);


        Set<String> rolesList = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .skills(user.getCourses().isEmpty()? null : user.getCourses().stream()
                        .map(Course::getCourseName)
                        .collect(Collectors.toSet()))
                .active(user.isActive())
                .userType(String.join(",",rolesList))
                .build();
    }



}
