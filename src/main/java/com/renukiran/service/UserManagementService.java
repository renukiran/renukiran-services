package com.renukiran.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.renukiran.dto.UserManagementResponse;
import com.renukiran.repository.TrainerRepository;
import com.renukiran.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final TrainerRepository trainerRepository;

    public List<UserManagementResponse> getAllUsers() {
        // 1. Fetch and map Trainers
        // List<UserManagementResponse> trainers = trainerRepository.findAll().stream()
        //         .map(t -> {
        //             UserManagementResponse res = new UserManagementResponse();
        //             res.setName(t.getFullName());
        //             res.setEmail(t.getEmail());
        //             res.setStatus(t.getStatus());
        //             res.setRole("TRAINER");
        //             return res;
        //         }).collect(Collectors.toList());

        // 2. Fetch and map Staff
        // List<UserManagementResponse> staff = staffRepository.findAll().stream()
        //         .map(s -> {
        //             UserManagementResponse res = new UserManagementResponse();
        //             res.setName(s.getFullName());
        //             res.setEmail(s.getEmail());
        //             res.setStatus(s.getStatus());
        //             res.setRole("STAFF");
        //             return res;
        //         }).collect(Collectors.toList());

        // 3. Combine both lists
        List<UserManagementResponse> allUsers = new ArrayList<>();
        // allUsers.addAll(trainers);
        // allUsers.addAll(staff);
        
        return allUsers;
    }
    
}
