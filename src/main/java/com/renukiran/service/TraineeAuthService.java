package com.renukiran.service;

import com.renukiran.dto.SignInResponse;
import com.renukiran.exception.AuthenticationException;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Service for trainee authentication
 */
@Service
public class TraineeAuthService {

    public SignInResponse authenticate(String username, String password) {
        // Validate credentials
        if (isValidCredentials(username, password)) {
            String token = generateToken(username);
            return new SignInResponse(
                true,
                "Authentication successful",
                token,
                200
            );
        } else {
            throw new AuthenticationException("Invalid username or password", 401);
        }
    }

    private boolean isValidCredentials(String username, String password) {
//        return isValidGmailAddress(username) && isValidPassword(password);
        return true; // Placeholder for actual validation logic
    }

    // Check for DB - This is a placeholder for actual database validation logic
//    private boolean isValidGmailAddress(String username) {
//        String gmailPattern = "^[a-zA-Z0-9._-]+@gmail\\\\.com$";
//        return username != null && username.matches(gmailPattern);
//    }

//    private boolean isValidPassword(String password) {
//        if (password == null || password.length() <= 8) {
//            return false;
//        }
//
//        boolean hasUpperCase = password.matches(".*[A-Z].*");
//        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
//        boolean hasNumber = password.matches(".*\\d.*");
//
//        return hasUpperCase && hasSpecialChar && hasNumber;
//    }

    private String generateToken(String username) {
        return "Bearer " + UUID.randomUUID().toString();
    }
}

