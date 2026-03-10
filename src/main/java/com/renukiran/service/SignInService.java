package com.renukiran.service;

import com.renukiran.dto.SignInRequest;
import com.renukiran.dto.SignInResponse;
import com.renukiran.entity.Users;
import com.renukiran.entity.BulkUsers;
import com.renukiran.repository.SignUpRepository;
import com.renukiran.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SignInService {

    @Autowired
    private SignUpRepository signUpRepository;

    @Autowired
    private UserRepository userRepository;

    public SignInResponse signIn(SignInRequest request) {

        Optional<Users> userOptional =
                signUpRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {

            Users user = userOptional.get();
            if (!user.getPassword().equals(request.getPassword())){
                return new SignInResponse(
                    false, 
                    "Invalid password", 
                    null, 
                    401
                );
            }

            String token = String.valueOf(user.getId());
            return new SignInResponse(
                true, 
                "Login successful for ID: "+token, 
                null, 
                200
            );
        }

        Optional<BulkUsers> bulkUserOptional =
                userRepository.findByUsername(request.getUsername());

        if (bulkUserOptional.isPresent()) {

            BulkUsers user = bulkUserOptional.get();

            if (!user.getPassword().equals(request.getPassword())) {
                return new SignInResponse(
                    false, 
                    "Invalid password", 
                    null, 
                    401
                );
            }

            String token = String.valueOf(user.getId());

            return new SignInResponse(
                true, 
                "Login successful for ID: "+token, 
                null, 
                200);
        }

        return new SignInResponse(
            false, 
            "User not found", 
            null, 
            404
        );
    }
}