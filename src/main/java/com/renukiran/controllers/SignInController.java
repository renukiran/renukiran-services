package com.renukiran.controllers;

import com.renukiran.dto.SignInRequest;
import com.renukiran.dto.SignInResponse;
import com.renukiran.entity.Users;
import com.renukiran.service.SignInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for trainee sign-in API endpoint
 * Accepts only HTTPS requests
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SignInController {

    @Autowired
    private SignInService signInService;

    @PostMapping("/sign-in")
    public SignInResponse signIn(@Valid @RequestBody SignInRequest request) {
        return signInService.signIn(request);
    }

}

