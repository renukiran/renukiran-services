package com.renukiran.dto;

import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.enums.AdmissionStatus;
import com.renukiran.enums.PreferredExperienceTrack;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;

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
    private LocalDate createdDate;
    private String status;
    private String courseName;

    public static CandidateResponse from(ApplicationForm af) {
        Admission highestAdmission = af.getAdmissions() == null
                ? null
                : af.getAdmissions().stream()
                        .filter(admission -> admission != null && admission.getStatus() != null)
                        .max(Comparator.comparingInt(admission -> admission.getStatus().ordinal()))
                        .orElse(null);

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
                .createdDate(af.getCreatedDate())
                .status(resolveStatus(highestAdmission))
                .courseName(resolveCourseName(af, highestAdmission))
                .build();
    }

    private static String resolveStatus(Admission admission) {
        AdmissionStatus status = admission != null ? admission.getStatus() : null;
        return status != null ? status.name() : AdmissionStatus.NEW.name();
    }

    private static String resolveCourseName(ApplicationForm af, Admission admission) {
        if (admission != null && admission.getBatch() != null && admission.getBatch().getCourse() != null) {
            return admission.getBatch().getCourse().getCourseName();
        }

        PreferredExperienceTrack track = af.getPreferredExperienceTrack();
        if (track == null) {
            return null;
        }

        return switch (track) {
            case TAILORING -> "Tailoring";
            case BEAUTY_AND_GROOMING -> "Beauty & Grooming";
            case FOOD_BUSINESS -> "Food Business";
            case HANDICRAFT -> "Handicraft";
            case HOME_BASED_PRODUCTION -> "Home-Based Production";
            case OTHER -> "Other";
        };
    }
}
