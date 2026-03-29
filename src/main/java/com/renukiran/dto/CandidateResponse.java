package com.renukiran.dto;

import com.renukiran.entity.Candidate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CandidateResponse {
    private Long candidateId;
    private String name;
    private LocalDate dob;
    private String gender;
    private String guardianName;
    private String qualification;
    private String occupation;
    private String mobile;
    private String address;
    private String category;
    private String skills;

    public static CandidateResponse from(Candidate c) {
        return CandidateResponse.builder()
                .candidateId(c.getCandidateId())
                .name(c.getName())
                .dob(c.getDob())
                .gender(c.getGender())
                .guardianName(c.getGuardianName())
                .qualification(c.getQualification())
                .occupation(c.getOccupation())
                .mobile(c.getMobile())
                .address(c.getAddress())
                .category(c.getCategory())
                .skills(c.getSkills())
                .build();
    }
}
