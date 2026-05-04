package com.renukiran.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Batch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDetailResponse {
    private Long id;
    private String fullName;
    private LocalDate createdDate;
    private int age;
    private String fatherOrHusbandName;
    private String mobileNumber;
    private String alternateMobileNumber;
    private String fullAddress;
    private String isLocalResidentOfGarhi;
    private String aadharNumber;
    private String isBankAccountAvailable;
    private String casteCategory;
    private int totalFamilyMembers;
    private int workingFamilyMembers;
    private int monthlyHouseholdIncome;
    private String primarySourceOfIncome;
    private String housingType;
    private String govtSchemeRationCardAvailed;
    private String govtSchemeWidowPensionAvailed;
    private String govtSchemeOldAgePensionAvailed;
    private String govtSchemeJanDhanAccountAvailed;
    private String govtSchemeUjjawalaAvailed;
    private String govtSchemeAnyOtherGovernmentSchemeAvailed;
    private String govtSchemeOtherDetails;
    private String migrationRisk;
    private String educationLevel;
    private String stitchingExperience;
    private String sewingMachineAtHome;
    private String beautyParlorExperience;
    private String foodBusinessExperience;
    @JsonProperty("HandicraftExperience")
    private String handicraftExperience;
    private String previousSkillTraining;
    private String preferredExperienceTrack;
    private String distanceTrainingCenter;
    private String motivationForJoiningTraining;
    private String motivationForJoiningTrainingDetailsOthersReason;
    private String ecoSituExtremeLowIncome;
    private String ecoSituSingleMotherOrWidow;
    private String ecoSituNoStableIncome;
    private String ecoSituHighFinancialStress;
    private String ecoSituFamilyDependentOnHer;
    private String ecoSituOther;
    private String ecoSituOtherDetails;
    private String learningFoundationLevelTraining;
    private String learningMachineSupportNeeded;
    private String learningConfidenceBuilding;
    private String learningSpeedImprovement;
    private String learningFinishingOrQualityControl;
    private String learningBusinessBasics;
    private String learningOther;
    private String learningOtherDetails;
    private String aspirationStartHomeKitchen;
    private String aspirationWorkMobileParlor;
    private String aspirationJoinGarmentJobWork;
    private String aspirationStartBoutiqueOrHomeStitching;
    private String aspirationJoinHandicraftWork;
    private String aspirationStartMicroEnterprise;
    private String aspirationJoinSHGAfter6Months;
    private String aspirationOther;
    private String aspirationOtherDetails;
    private String willingToParticipateInProduction;
    private List<CandidateAdmissionResponse> admissions;

    public static CandidateDetailResponse from(ApplicationForm application) {
        return CandidateDetailResponse.builder()
                .id(application.getId())
                .fullName(application.getFullName())
                .createdDate(application.getCreatedDate())
                .age(application.getAge())
                .fatherOrHusbandName(application.getFatherOrHusbandName())
                .mobileNumber(application.getMobileNumber())
                .alternateMobileNumber(application.getAlternateMobileNumber())
                .fullAddress(application.getFullAddress())
                .isLocalResidentOfGarhi(enumName(application.getIsLocalResidentOfGarhi()))
                .aadharNumber(application.getAadharNumber())
                .isBankAccountAvailable(enumName(application.getIsBankAccountAvailable()))
                .casteCategory(enumName(application.getCasteCategory()))
                .totalFamilyMembers(application.getTotalFamilyMembers())
                .workingFamilyMembers(application.getWorkingFamilyMembers())
                .monthlyHouseholdIncome(application.getMonthlyHouseholdIncome())
                .primarySourceOfIncome(application.getPrimarySourceOfIncome())
                .housingType(enumName(application.getHousingType()))
                .govtSchemeRationCardAvailed(enumName(application.getGovtSchemeRationCardAvailed()))
                .govtSchemeWidowPensionAvailed(enumName(application.getGovtSchemeWidowPensionAvailed()))
                .govtSchemeOldAgePensionAvailed(enumName(application.getGovtSchemeOldAgePensionAvailed()))
                .govtSchemeJanDhanAccountAvailed(enumName(application.getGovtSchemeJanDhanAccountAvailed()))
                .govtSchemeUjjawalaAvailed(enumName(application.getGovtSchemeUjjawalaAvailed()))
                .govtSchemeAnyOtherGovernmentSchemeAvailed(enumName(application.getGovtSchemeAnyOtherGovernmentSchemeAvailed()))
                .govtSchemeOtherDetails(enumName(application.getGovtSchemeOtherDetails()))
                .migrationRisk(enumName(application.getMigrationRisk()))
                .educationLevel(enumName(application.getEducationLevel()))
                .stitchingExperience(enumName(application.getStitchingExperience()))
                .sewingMachineAtHome(enumName(application.getSewingMachineAtHome()))
                .beautyParlorExperience(enumName(application.getBeautyParlorExperience()))
                .foodBusinessExperience(enumName(application.getFoodBusinessExperience()))
                .handicraftExperience(enumName(application.getHandicraftExperience()))
                .previousSkillTraining(application.getPreviousSkillTraining())
                .preferredExperienceTrack(enumName(application.getPreferredExperienceTrack()))
                .distanceTrainingCenter(application.getDistanceTrainingCenter())
                .motivationForJoiningTraining(enumName(application.getMotivationForJoiningTraining()))
                .motivationForJoiningTrainingDetailsOthersReason(application.getMotivationForJoiningTrainingDetailsOthersReason())
                .ecoSituExtremeLowIncome(enumName(application.getEcoSituExtremeLowIncome()))
                .ecoSituSingleMotherOrWidow(enumName(application.getEcoSituSingleMotherOrWidow()))
                .ecoSituNoStableIncome(enumName(application.getEcoSituNoStableIncome()))
                .ecoSituHighFinancialStress(enumName(application.getEcoSituHighFinancialStress()))
                .ecoSituFamilyDependentOnHer(enumName(application.getEcoSituFamilyDependentOnHer()))
                .ecoSituOther(enumName(application.getEcoSituOther()))
                .ecoSituOtherDetails(application.getEcoSituOtherDetails())
                .learningFoundationLevelTraining(enumName(application.getLearningFoundationLevelTraining()))
                .learningMachineSupportNeeded(enumName(application.getLearningMachineSupportNeeded()))
                .learningConfidenceBuilding(application.getLearningConfidenceBuilding())
                .learningSpeedImprovement(enumName(application.getLearningSpeedImprovement()))
                .learningFinishingOrQualityControl(enumName(application.getLearningFinishingOrQualityControl()))
                .learningBusinessBasics(enumName(application.getLearningBusinessBasics()))
                .learningOther(enumName(application.getLearningOther()))
                .learningOtherDetails(application.getLearningOtherDetails())
                .aspirationStartHomeKitchen(enumName(application.getAspirationStartHomeKitchen()))
                .aspirationWorkMobileParlor(enumName(application.getAspirationWorkMobileParlor()))
                .aspirationJoinGarmentJobWork(enumName(application.getAspirationJoinGarmentJobWork()))
                .aspirationStartBoutiqueOrHomeStitching(enumName(application.getAspirationStartBoutiqueOrHomeStitching()))
                .aspirationJoinHandicraftWork(enumName(application.getAspirationJoinHandicraftWork()))
                .aspirationStartMicroEnterprise(enumName(application.getAspirationStartMicroEnterprise()))
                .aspirationJoinSHGAfter6Months(enumName(application.getAspirationJoinSHGAfter6Months()))
                .aspirationOther(enumName(application.getAspirationOther()))
                .aspirationOtherDetails(application.getAspirationOtherDetails())
                .willingToParticipateInProduction(enumName(application.getWillingToParticipateInProduction()))
                .admissions(mapAdmissions(application))
                .build();
    }

    private static List<CandidateAdmissionResponse> mapAdmissions(ApplicationForm application) {
        if (application.getAdmissions() == null) {
            return List.of();
        }

        return application.getAdmissions().stream()
                .filter(admission -> admission != null)
                .sorted(Comparator.comparingInt(admission -> admission.getStatus() != null ? admission.getStatus().ordinal() : -1))
                .map(CandidateAdmissionResponse::from)
                .toList();
    }

    private static String enumName(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateAdmissionResponse {
        private String admissionNumber;
        private String status;
        private CandidateBatchResponse batch;
        private Integer attendancePercentage; // added

        static CandidateAdmissionResponse from(Admission admission) {
            return CandidateAdmissionResponse.builder()
                    .admissionNumber(admission.getAdmissionNumber())
                    .status(enumName(admission.getStatus()))
                    .batch(CandidateBatchResponse.from(admission.getBatch()))
                    .attendancePercentage(admission.getAttendancePercentage())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateBatchResponse {
        private Long id;
        private String batchName;
        private String timing;
        private LocalDate startDate;
        private LocalDate endDate;
        private CandidateCourseResponse course;
        private CandidateTrainerResponse trainer;

        static CandidateBatchResponse from(Batch batch) {
            if (batch == null) {
                return null;
            }

            return CandidateBatchResponse.builder()
                    .id(batch.getId())
                    .batchName(batch.getBatchName())
                    .timing(batch.getTiming())
                    .startDate(batch.getStartDate())
                    .endDate(batch.getEndDate())
                    .course(batch.getCourse() == null ? null : CandidateCourseResponse.builder()
                            .courseName(batch.getCourse().getCourseName())
                            .build())
                    .trainer(batch.getTrainer() == null ? null : CandidateTrainerResponse.builder()
                            .name(batch.getTrainer().getFirstName()+" "+batch.getTrainer().getLastName())
                            .build())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateCourseResponse {
        private String courseName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CandidateTrainerResponse {
        private String name;
    }
}