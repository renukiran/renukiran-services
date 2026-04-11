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
        String userType = user.getUserType();
        if (!"ADMIN".equalsIgnoreCase(userType) && !"TRAINER".equalsIgnoreCase(userType) && !"COORDINATOR".equalsIgnoreCase(userType)) {
            return new SignInResponse(false, "Unauthorized access", null, 403);
        }

        if( (request.getUserName().equals(user.getUsername())) || (request.getPassword().equals(user.getPassword())) ){
            return new SignInResponse(false,"Invalid Credentials",null,403);
        }

        String fullName = ((user.getFirstName() != null ? user.getFirstName() : "") + " " +
                (user.getLastName() != null ? user.getLastName() : "")).trim();
        if (fullName.isEmpty()) fullName = user.getUsername();

        SignInResponse resp = new SignInResponse(true, "Admin login successful", null, 200);
        resp.setUserType(user.getUserType());
        resp.setUserName(fullName);
        resp.setUserId(user.getId());
        return resp;
    }
}