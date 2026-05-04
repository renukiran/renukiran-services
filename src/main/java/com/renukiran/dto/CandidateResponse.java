package com.renukiran.dto;

import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.enums.AdmissionStatus;
import com.renukiran.enums.PreferredExperienceTrack;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private Integer attendancePercentage; // overall (max across admissions)
    private List<AdmissionAttendance> admissions; // per-admission attendance summaries

    public static CandidateResponse from(ApplicationForm af) {
        Admission highestAdmission = af.getAdmissions() == null
                ? null
                : af.getAdmissions().stream()
                        .filter(admission -> admission != null && admission.getStatus() != null)
                        .max(Comparator.comparingInt(admission -> admission.getStatus().ordinal()))
                        .orElse(null);

        List<AdmissionAttendance> admissionAttendances = (af.getAdmissions() == null) ? List.of() : af.getAdmissions().stream()
                .filter(admission -> admission != null)
                .map(admission -> AdmissionAttendance.builder()
                        .admissionNumber(admission.getAdmissionNumber())
                        .status(admission.getStatus() != null ? admission.getStatus().name() : null)
                        .batchId(admission.getBatch() != null ? admission.getBatch().getId() : null)
                        .batchName(admission.getBatch() != null ? admission.getBatch().getBatchName() : null)
                        .attendancePercentage(admission.getAttendancePercentage())
                        .build())
                .collect(Collectors.toList());

        // determine overall max attendance across admissions
        Integer overall = null;
        for (AdmissionAttendance aa : admissionAttendances) {
            if (aa == null) continue;
            Integer p = aa.getAttendancePercentage();
            if (p != null) {
                if (overall == null || p > overall) overall = p;
            }
        }

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
                .attendancePercentage(overall)
                .admissions(admissionAttendances)
                .build();
    }

    public static CandidateResponse from(ApplicationForm af, Integer attendancePercentage) {
        Admission highestAdmission = af.getAdmissions() == null
                ? null
                : af.getAdmissions().stream()
                        .filter(admission -> admission != null && admission.getStatus() != null)
                        .max(Comparator.comparingInt(admission -> admission.getStatus().ordinal()))
                        .orElse(null);

        List<AdmissionAttendance> admissionAttendances = (af.getAdmissions() == null) ? List.of() : af.getAdmissions().stream()
                .filter(admission -> admission != null)
                .map(admission -> AdmissionAttendance.builder()
                        .admissionNumber(admission.getAdmissionNumber())
                        .status(admission.getStatus() != null ? admission.getStatus().name() : null)
                        .batchId(admission.getBatch() != null ? admission.getBatch().getId() : null)
                        .batchName(admission.getBatch() != null ? admission.getBatch().getBatchName() : null)
                        .attendancePercentage(admission.getAttendancePercentage())
                        .build())
                .collect(Collectors.toList());

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
                .attendancePercentage(attendancePercentage)
                .admissions(admissionAttendances)
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

    @Getter
    @Builder
    public static class AdmissionAttendance {
        private String admissionNumber;
        private String status;
        private Long batchId;
        private String batchName;
        private Integer attendancePercentage;
    }
}
