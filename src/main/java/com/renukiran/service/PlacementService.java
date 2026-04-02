package com.renukiran.service;

import com.renukiran.dto.PlacementRequest;
import com.renukiran.dto.PlacementResponse;
import com.renukiran.entity.Placement;
import com.renukiran.repository.PlacementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlacementService {

    private final PlacementRepository placementRepository;

    public List<PlacementResponse> getAllPlacements() {
        return placementRepository.findAll().stream()
                .map(PlacementResponse::from)
                .toList();
    }

    public PlacementResponse getPlacementById(Long id) {
        Placement placement = placementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Placement not found with id: " + id));
        return PlacementResponse.from(placement);
    }

    public PlacementResponse createPlacement(PlacementRequest request) {
        Placement placement = Placement.builder()
                .name(request.name())
                .employer(request.employer())
                .role(request.role())
                .salary(request.salary())
                .placedDate(request.placedDate())
                .status(request.status() != null ? request.status() : "Active")
                .course(request.course())
                .batch(request.batch())
                .assessment(request.assessment())
                .build();
        return PlacementResponse.from(placementRepository.save(placement));
    }

    public PlacementResponse updatePlacement(Long id, PlacementRequest request) {
        Placement placement = placementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Placement not found with id: " + id));
        if (request.name() != null)       placement.setName(request.name());
        if (request.employer() != null)   placement.setEmployer(request.employer());
        if (request.role() != null)       placement.setRole(request.role());
        if (request.salary() != null)     placement.setSalary(request.salary());
        if (request.placedDate() != null) placement.setPlacedDate(request.placedDate());
        if (request.status() != null)     placement.setStatus(request.status());
        if (request.course() != null)     placement.setCourse(request.course());
        if (request.batch() != null)      placement.setBatch(request.batch());
        if (request.assessment() != null) placement.setAssessment(request.assessment());
        return PlacementResponse.from(placementRepository.save(placement));
    }

    public void deletePlacement(Long id) {
        if (!placementRepository.existsById(id)) {
            throw new RuntimeException("Placement not found with id: " + id);
        }
        placementRepository.deleteById(id);
    }
}
