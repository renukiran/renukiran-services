package com.renukiran.service;

import com.renukiran.dto.BulkRegistrationRequest;
import com.renukiran.dto.BulkRegistrationResponse;
import com.renukiran.dto.UserRegistrationResult;
import com.renukiran.entity.BulkUsers;
import com.renukiran.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BulkRegistrationService {

    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;

    public BulkRegistrationResponse registerUsers(List<BulkRegistrationRequest> requests) {

        List<UserRegistrationResult> results = new ArrayList<>();
        List<BulkUsers> usersToSave = new ArrayList<>();

        int success = 0;
        int failed = 0;

        for (BulkRegistrationRequest req : requests) {

            if (userRepository.existsByUsername(req.getUsername())) {

                results.add(UserRegistrationResult.builder()
                        .username(req.getUsername())
                        .status("FAILED")
                        .message("Username already exists")
                        .build());

                failed++;
                continue;
            }

            if (userRepository.existsByEmail(req.getEmail())) {

                results.add(UserRegistrationResult.builder()
                        .username(req.getUsername())
                        .status("FAILED")
                        .message("Email already exists")
                        .build());

                failed++;
                continue;
            }

            BulkUsers user = BulkUsers.builder()
                    .username(req.getUsername())
                    .password(req.getPassword())
                    .email(req.getEmail())
                    .phone(req.getPhone())
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .dateOfBirth(LocalDate.parse(req.getDateOfBirth()))
                    .address(req.getAddress())
                    .preferredCourseCode(req.getPreferredCourseCode())
                    .userType(req.getUserType())
                    .build();

            usersToSave.add(user);

            results.add(UserRegistrationResult.builder()
                    .username(req.getUsername())
                    .status("SUCCESS")
                    .message("User registered successfully")
                    .build());

            success++;
        }

        if (!usersToSave.isEmpty()) {
            userRepository.saveAll(usersToSave);
        }

        return BulkRegistrationResponse.builder()
                .total(requests.size())
                .registered(success)
                .failed(failed)
                .results(results)
                .build();
    }
}

