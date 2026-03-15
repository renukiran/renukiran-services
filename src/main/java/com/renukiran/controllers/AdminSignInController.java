package com.renukiran.controllers;

import com.renukiran.dto.SignInRequest;
import com.renukiran.dto.SignInResponse;
import com.renukiran.service.AdminSignInService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for trainee sign-in API endpoint
 * Accepts only HTTP requests
 */
@RestController
@RequestMapping("/auth")
public class AdminSignInController {

    @Autowired
    private AdminSignInService adminsignInService;

    @PostMapping("/admin")
    public SignInResponse signIn(@Valid @RequestBody SignInRequest request) {
        System.out.println("ADMIN LOGIN HIT");
        return adminsignInService.signIn(request);
    }

}

