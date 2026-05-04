package com.renukiran.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.renukiran.enums.*;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApplicationForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;

    private LocalDate createdDate;

    private int age;

    private String fatherOrHusbandName;

    private String mobileNumber;
    private String alternateMobileNumber;

    private String fullAddress;

    private Status isLocalResidentOfGarhi;

    private String aadharNumber;

    private Status isBankAccountAvailable;

    private CasteCategory casteCategory;


    private int totalFamilyMembers;

    private int workingFamilyMembers;

    private int monthlyHouseholdIncome;

    private String primarySourceOfIncome;

    @Enumerated(EnumType.STRING)
    private HousingType housingType;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeRationCardAvailed;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeWidowPensionAvailed;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeOldAgePensionAvailed;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeJanDhanAccountAvailed;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeUjjawalaAvailed;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeAnyOtherGovernmentSchemeAvailed;
    @Enumerated(EnumType.STRING)
    private Status govtSchemeOtherDetails;

    @Enumerated(EnumType.STRING)
    private MigrationRiskForSixMonths migrationRisk;
    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;
    @Enumerated(EnumType.STRING)
    private StitchingExperience stitchingExperience;
    @Enumerated(EnumType.STRING)
    private Status sewingMachineAtHome;
    @Enumerated(EnumType.STRING)
    private Status beautyParlorExperience;
    @Enumerated(EnumType.STRING)
    private Status foodBusinessExperience;
    @Enumerated(EnumType.STRING)
    private Status HandicraftExperience;

    private String previousSkillTraining;
    @Enumerated(EnumType.STRING)
    private PreferredExperienceTrack preferredExperienceTrack;
    private String distanceTrainingCenter;
    @Enumerated(EnumType.STRING)
    private MotivationForJoiningTraining motivationForJoiningTraining;

    private String motivationForJoiningTrainingDetailsOthersReason;

    @Enumerated(EnumType.STRING)
    private EconomicSituation ecoSituExtremeLowIncome;

    @Enumerated(EnumType.STRING)
    private Status ecoSituSingleMotherOrWidow;

    @Enumerated(EnumType.STRING)
    private Status ecoSituNoStableIncome;
    @Enumerated(EnumType.STRING)
    private Status ecoSituHighFinancialStress;
    @Enumerated(EnumType.STRING)
    private Status ecoSituFamilyDependentOnHer;
    @Enumerated(EnumType.STRING)
    private Status ecoSituOther;

    private String ecoSituOtherDetails;
    @Enumerated(EnumType.STRING)
    private Status learningFoundationLevelTraining;

    @Enumerated(EnumType.STRING)
    private Status learningMachineSupportNeeded;


    private String learningConfidenceBuilding;
    @Enumerated(EnumType.STRING)
    private Status learningSpeedImprovement;
    @Enumerated(EnumType.STRING)
    private Status learningFinishingOrQualityControl;
    @Enumerated(EnumType.STRING)
    private Status learningBusinessBasics;
    @Enumerated(EnumType.STRING)
    private Status learningOther;

    private String learningOtherDetails;
    @Enumerated(EnumType.STRING)
    private Status aspirationStartHomeKitchen;
    @Enumerated(EnumType.STRING)
    private Status aspirationWorkMobileParlor;
    @Enumerated(EnumType.STRING)
    private Status aspirationJoinGarmentJobWork;
    @Enumerated(EnumType.STRING)
    private Status aspirationStartBoutiqueOrHomeStitching;
    @Enumerated(EnumType.STRING)
    private Status aspirationJoinHandicraftWork;
    @Enumerated(EnumType.STRING)
    private Status aspirationStartMicroEnterprise;
    @Enumerated(EnumType.STRING)
    private Status aspirationJoinSHGAfter6Months;
    @Enumerated(EnumType.STRING)
    private Status aspirationOther;

    private String aspirationOtherDetails;
    @Enumerated(EnumType.STRING)
    private Status willingToParticipateInProduction;

    // One-to-many mapping: an ApplicationForm can have multiple Admission records
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Admission> admissions = new ArrayList<>();



    @PrePersist
    void prePersist() {

        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
    }

}
