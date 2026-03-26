package com.renukiran.controllers;

import com.renukiran.dto.AdmissionRequest;
import com.renukiran.dto.AdmissionResponse;
import com.renukiran.service.AdmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admissions")
@RequiredArgsConstructor
public class AdmissionController {
    private final AdmissionService admissionService;


    @PostMapping
    public ResponseEntity<AdmissionResponse> createAdmission(@Valid @RequestBody AdmissionRequest admissionRequest){
        AdmissionResponse response = admissionService.registerAdmission(admissionRequest);
        return ResponseEntity.ok(response);
    }
}
