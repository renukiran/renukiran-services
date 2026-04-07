package com.renukiran.service;

import java.util.*;
import java.util.stream.Collectors;

import com.renukiran.dto.SignUpRequest;
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

    private final TrainerRepository trainerRepository;
    private final SignUpRepository signUpRepository;
    private final RoleRepository roleRepo;
    private final CourseRepository courseRepo;

    public List<UserManagementResponse> getAllUsers() {
        // 1. Fetch and map Trainers
        // List<UserManagementResponse> trainers = trainerRepository.findAll().stream()
        //         .map(t -> {
        //             UserManagementResponse res = new UserManagementResponse();
        //             res.setName(t.getFullName());
        //             res.setEmail(t.getEmail());
        //             res.setStatus(t.getStatus());
        //             res.setRole("TRAINER");
        //             return res;
        //         }).collect(Collectors.toList());

        // 2. Fetch and map Staff
        // List<UserManagementResponse> staff = staffRepository.findAll().stream()
        //         .map(s -> {
        //             UserManagementResponse res = new UserManagementResponse();
        //             res.setName(s.getFullName());
        //             res.setEmail(s.getEmail());
        //             res.setStatus(s.getStatus());
        //             res.setRole("STAFF");
        //             return res;
        //         }).collect(Collectors.toList());

        // 3. Combine both lists
        List<UserManagementResponse> allUsers = new ArrayList<>();
        // allUsers.addAll(trainers);
        // allUsers.addAll(staff);
        
        return allUsers;
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
                .skills(user.getCourses().stream()
                        .map(Course::getCourseName)
                        .collect(Collectors.toSet()))
                .active(user.isActive())
                .userType(String.join(",",rolesList))
                .build();
    }



}
