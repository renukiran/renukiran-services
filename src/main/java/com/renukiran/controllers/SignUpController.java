package com.renukiran.controllers;

import com.renukiran.dto.SignUpRequest;
import com.renukiran.dto.TrainerAvailabilityRequest;
import com.renukiran.dto.UserRequest;
import com.renukiran.dto.UserResponse;
import com.renukiran.entity.Users;
import com.renukiran.service.SignUpService;
import com.renukiran.service.UserManagementService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;
    private final UserManagementService userManagementService;

    @PostMapping("/trainers")
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody UserRequest request) {
        UserResponse userResponse = userManagementService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }


    @PostMapping("/{trainerUserId}/availability")
    public ResponseEntity<String> setTrainerAvailability(@PathVariable UUID trainerUserId, @Valid @RequestBody TrainerAvailabilityRequest request) {

        return ResponseEntity.ok("Trainer availability saved successfully");
    }

    @GetMapping("/trainers/by-tracking/{trackingNumber}")
    public ResponseEntity<Users> getTrainer(@NotNull @PathVariable Long trackingNumber ){
        Users trainer = signUpService.findTrainer(trackingNumber);
        return ResponseEntity.ok(trainer);
    }

    // ── User CRUD ─────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(signUpService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(signUpService.getUserById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(signUpService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(signUpService.updateUser(id, request));
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(signUpService.toggleUserStatus(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        signUpService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
