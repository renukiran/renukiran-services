package com.renukiran.controllers;

import com.renukiran.dto.TrainerDashboardResponse;
import com.renukiran.service.TrainerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerDashboardController {

    private final TrainerDashboardService trainerDashboardService;

    @GetMapping("/{trainerId}/dashboard")
    public ResponseEntity<TrainerDashboardResponse> getDashboard(@PathVariable Long trainerId) {
        return ResponseEntity.ok(trainerDashboardService.getDashboard(trainerId));
    }
}
