package com.renukiran.controllers;

import com.renukiran.dto.OcDashboardResponse;
import com.renukiran.service.OcDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/office-coordinator")
@RequiredArgsConstructor
public class OcDashboardController {

    private final OcDashboardService ocDashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<OcDashboardResponse> getDashboard(
            @RequestParam(required = false) String coordinatorName) {
        return ResponseEntity.ok(ocDashboardService.getDashboard(coordinatorName));
    }
}
