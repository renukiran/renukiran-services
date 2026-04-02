package com.renukiran.service;

import com.renukiran.dto.SignUpRequest;
import com.renukiran.dto.UserResponse;
import com.renukiran.entity.Users;
import com.renukiran.exception.DuplicateResourceException;
import com.renukiran.repository.SignUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SignUpService {

    private final SignUpRepository signUpRepository;
   // private final PasswordEncoder passwordEncoder;

    public Users signUp(SignUpRequest request){
        if (signUpRepository.existsByUsername(request.userName())) {
            throw new DuplicateResourceException("Username already exists: " + request.userName(), "DUPLICATE_USERNAME");
        }

        if (signUpRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email already registered: " + request.email(), "DUPLICATE_EMAIL");
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

    public List<UserResponse> getAllUsers() {
        return signUpRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse getUserById(Long id) {
        Users user = signUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponse.from(user);
    }

    public void deleteUser(Long id) {
        if (!signUpRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        signUpRepository.deleteById(id);
    }

    public UserResponse createUser(SignUpRequest request) {
        return UserResponse.from(signUp(request));
    }

    @Transactional
    public UserResponse updateUser(Long id, SignUpRequest request) {
        Users user = signUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(request.userName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setSkills(request.skills());
        user.setUserType(request.userType());
        return UserResponse.from(signUpRepository.save(user));
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        Users user = signUpRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setActive(!user.isActive());
        return UserResponse.from(signUpRepository.save(user));
    }
}
