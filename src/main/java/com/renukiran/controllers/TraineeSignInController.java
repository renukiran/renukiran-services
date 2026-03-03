package com.renukiran.controllers;

import com.renukiran.dto.SignInRequest;
import com.renukiran.dto.SignInResponse;
import com.renukiran.service.TraineeAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for trainee sign-in API endpoint
 * Accepts only HTTPS requests
 */
@RestController
@RequestMapping("/trainee")
public class TraineeSignInController {

    @Autowired
    private TraineeAuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(
            @Valid @RequestBody SignInRequest signInRequest) {

        SignInResponse response = authService.authenticate(
            signInRequest.getUsername(),
            signInRequest.getPassword()
        );

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }
}

