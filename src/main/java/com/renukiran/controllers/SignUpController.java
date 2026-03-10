package com.renukiran.controllers;

import com.renukiran.dto.SignUpRequest;
import com.renukiran.dto.TrainerAvailabilityRequest;
import com.renukiran.entity.Users;
import com.renukiran.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpService signUpService;

    @PostMapping()
    public ResponseEntity<String> signup(@Valid @RequestBody SignUpRequest request) {
        Users user = signUpService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Signup successful! Your account has been created.");
    }


    @PostMapping("/{trainerUserId}/availability")
    public ResponseEntity<String> setTrainerAvailability(@PathVariable UUID trainerUserId, @Valid @RequestBody TrainerAvailabilityRequest request) {

        return ResponseEntity.ok("Trainer availability saved successfully");
    }
}
