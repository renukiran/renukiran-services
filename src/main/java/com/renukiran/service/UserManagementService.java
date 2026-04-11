package com.renukiran.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.renukiran.dto.UserRequest;
import com.renukiran.dto.UserResponse;
import com.renukiran.entity.Course;
import com.renukiran.entity.Role;
import com.renukiran.entity.Users;
import com.renukiran.enums.RoleType;
import com.renukiran.exception.DuplicateResourceException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.*;
import org.springframework.stereotype.Service;

import com.renukiran.dto.UserManagementResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

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
                    res.setUserId(String.valueOf(user.getId()));
                    res.setName(user.getUsername());
                    res.setEmail(user.getEmail());
                    res.setStatus(user.isActive() ? "ACTIVE" : "INACTIVE");
                    res.setRole(Objects.nonNull(user.getRole()) ? user.getRole().getName().name() : null);
                   // res.setDeleted(user.getDeleted());
                    return res;
                }).collect(Collectors.toList());
    }

    public UserResponse createUser(UserRequest dto) {

        if (signUpRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists with " + dto.getEmail(), "EMAIL_EXIST");
        }

        Role role = roleRepo.findByName(dto.getRole())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found for " + dto.getRole().name(), "ROLE_NOT_EXIST"));

        Users user = Users.builder()
                .username(dto.getFullName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .role(role)
                .userType(role.getName().name().toUpperCase())
                .build();

        if (dto.getRole() == RoleType.TRAINER) {

            List<Course> courses = courseRepo.findAllById(dto.getCourseIds());

            if (courses.size() != dto.getCourseIds().size()) {
                throw new ResourceNotFoundException("Invalid CourseIds", "INVALID_COURSE");
            }

            user.setCourses(new HashSet<>(courses));
        }

        signUpRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .skills(CollectionUtils.isEmpty(user.getCourses()) ? null : user.getCourses().stream()
                        .map(Course::getCourseName)
                        .collect(Collectors.toSet()))
                .active(user.isActive())
                .userType(user.getUserType())
                .build();
    }


    public void deleteUser(Long id, Long deletedById) {
        // 🔍 Fetch target user (including deleted check)
        Users user = signUpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found", "USER_NOT_FOUND"));
        if (Boolean.TRUE.equals(user.getDeleted())) {
            throw new ResourceNotFoundException("User is already deleted", "USER_ALREADY_DELETED");
        }

        // 🔍 Fetch actor (who deletes)
        Users deletedBy = signUpRepository.findById(deletedById)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid deleted By User: "+deletedById, "INVALID_DELETED-BY_USER"));

        if (Boolean.TRUE.equals(deletedBy.getDeleted())) {
            throw new ResourceNotFoundException("DeletedBy user is inactive", "INACTIVE_USER");
        }

        // Prevent self-delete
        if (id.equals(deletedById)) {
            throw new ResourceNotFoundException("User cannot delete themselves", "DO_NOT_DELETE_BY_THEMSELVES");
        }

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(deletedBy);
        user.setActive(Boolean.FALSE);
        signUpRepository.save(user);
    }
}
