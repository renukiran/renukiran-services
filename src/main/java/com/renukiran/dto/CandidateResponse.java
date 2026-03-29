package com.renukiran.dto;

import com.renukiran.entity.ApplicationForm;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CandidateResponse {
    private Long candidateId;
    private String name;
    private String guardianName;
    private String qualification;
    private String occupation;
    private String mobile;
    private String address;
    private String category;
    private String skills;

    public static CandidateResponse from(ApplicationForm af) {
        return CandidateResponse.builder()
                .candidateId(af.getId())
                .name(af.getFullName())
                .guardianName(af.getFatherOrHusbandName())
                .qualification(af.getEducationLevel() != null ? af.getEducationLevel().name() : null)
                .occupation(af.getPrimarySourceOfIncome())
                .mobile(af.getMobileNumber())
                .address(af.getFullAddress())
                .category(af.getCasteCategory() != null ? af.getCasteCategory().name() : null)
                .skills(af.getPreviousSkillTraining())
                .build();
    }
}
