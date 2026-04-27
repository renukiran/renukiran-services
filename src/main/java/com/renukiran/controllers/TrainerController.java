package com.renukiran.controllers;

import com.renukiran.dto.UserManagementResponse;
import com.renukiran.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<List<UserManagementResponse>> getAllTrainers() {
        return ResponseEntity.ok(userManagementService.getTrainers());
    }
}
