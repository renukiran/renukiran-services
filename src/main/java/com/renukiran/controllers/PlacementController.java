package com.renukiran.controllers;

import com.renukiran.dto.PlacementRequest;
import com.renukiran.dto.PlacementResponse;
import com.renukiran.service.PlacementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/placements")
@RequiredArgsConstructor
public class PlacementController {

    private final PlacementService placementService;

    @GetMapping
    public ResponseEntity<List<PlacementResponse>> getAllPlacements() {
        return ResponseEntity.ok(placementService.getAllPlacements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacementResponse> getPlacementById(@PathVariable Long id) {
        return ResponseEntity.ok(placementService.getPlacementById(id));
    }

    @PostMapping
    public ResponseEntity<PlacementResponse> createPlacement(@Valid @RequestBody PlacementRequest request) {
        PlacementResponse created = placementService.createPlacement(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlacementResponse> updatePlacement(@PathVariable Long id,
                                                             @Valid @RequestBody PlacementRequest request) {
        return ResponseEntity.ok(placementService.updatePlacement(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlacement(@PathVariable Long id) {
        placementService.deletePlacement(id);
        return ResponseEntity.noContent().build();
    }
}
