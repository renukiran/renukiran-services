package com.renukiran.service;

import com.renukiran.dto.SignUpRequest;
import com.renukiran.entity.Users;
import com.renukiran.repository.SignUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final SignUpRepository signUpRepository;
   // private final PasswordEncoder passwordEncoder;

    public Users signUp(SignUpRequest request){
        if (signUpRepository.existsByUsername(request.userName())) {
            throw new RuntimeException("Username already exists");
        }

        if (signUpRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }
        Users trainer = Users.builder()
                .username(request.userName())
                .password(request.password())
                .email(request.email())
                .phone(request.phone())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .skills(request.skills())
                .userType(request.userType())
                .build();

        return signUpRepository.save(trainer);
    }

    public Users findTrainer(Long trackingId){
        return signUpRepository.findById(trackingId).orElseThrow(() ->
                new RuntimeException("Trainer not found for tracking number: " +trackingId));

    }
}
