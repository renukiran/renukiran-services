package com.renukiran.service;

import com.renukiran.dto.SignInRequest;
import com.renukiran.dto.SignInResponse;
import com.renukiran.entity.Users;
import com.renukiran.repository.SignUpRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminSignInService {

    @Autowired
    private SignUpRepository signUpRepository;

    public SignInResponse signIn(SignInRequest request) {

        Optional<Users> userOptional =
                signUpRepository.findByUsername(request.getUserName());

        if (userOptional.isEmpty()) {
            return new SignInResponse(
                false, 
                "User is not Registered!", 
                null, 
                404
            );
        }
        
        Users user = userOptional.get();

        if (!"ADMIN".equalsIgnoreCase(user.getUserType())) {
            return new SignInResponse(false, "Unauthorized access", null, 403);
        }

        // // Check encrypted password
        // if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        //     return new SignInResponse(false, "Invalid password", null, 401);
        // }

        return new SignInResponse(
                true,
                "Admin login successful",
                null,
                200
        );
    }
}