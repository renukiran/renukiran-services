package com.renukiran.dto;

import com.renukiran.entity.Placement;
import com.renukiran.entity.PlacementFollowup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PlacementResponse {
    private Long id;
    private String name;
    private String employer;
    private String role;
    private Integer salary;
    private String placedDate;
    private String status;
    private String course;
    private String batch;
    private String assessment;
    private List<PlacementFollowupResponse> followups;

    public static PlacementResponse from(Placement p) {
        List<PlacementFollowupResponse> followupDtos = p.getFollowups().stream()
                .map(PlacementFollowupResponse::from)
                .toList();
        return PlacementResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .employer(p.getEmployer())
                .role(p.getRole())
                .salary(p.getSalary())
                .placedDate(p.getPlacedDate())
                .status(p.getStatus())
                .course(p.getCourse())
                .batch(p.getBatch())
                .assessment(p.getAssessment())
                .followups(followupDtos)
                .build();
    }
}
