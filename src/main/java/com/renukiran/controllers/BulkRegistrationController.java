package com.renukiran.controllers;

import com.renukiran.dto.BulkRegistrationRequest;
import com.renukiran.dto.BulkRegistrationResponse;
import com.renukiran.dto.SignUpRequest;
import com.renukiran.entity.Users;
import com.renukiran.service.BulkRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BulkRegistrationController {

    private final BulkRegistrationService service;

    @PostMapping("/bulk/registrations")
    public ResponseEntity<BulkRegistrationResponse> registerBulkUsers( @Valid @RequestBody List<BulkRegistrationRequest> requests) {
        BulkRegistrationResponse response = service.registerUsers(requests);
        return ResponseEntity.ok(response);
    }
}
