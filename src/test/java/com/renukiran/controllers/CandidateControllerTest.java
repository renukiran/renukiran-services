package com.renukiran.controllers;

import com.renukiran.entity.Admission;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Batch;
import com.renukiran.entity.Course;
import com.renukiran.entity.Trainer;
import com.renukiran.enums.AdmissionStatus;
import com.renukiran.enums.CasteCategory;
import com.renukiran.enums.EducationLevel;
import com.renukiran.enums.PreferredExperienceTrack;
import com.renukiran.enums.Status;
import com.renukiran.repository.ApplicationFormRepository;
import com.renukiran.repository.BatchRepository;
import com.renukiran.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
@Import(CandidateService.class)
class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
    private ApplicationFormRepository applicationFormRepository;

        @MockitoBean
    private BatchRepository batchRepository;

    @Test
    void getAllCandidatesReturnsExpandedListContract() throws Exception {
        ApplicationForm assignedCandidate = buildCandidate(101L, "Asha Devi");
        assignedCandidate.setAdmissions(new ArrayList<>(List.of(buildAdmission(
                assignedCandidate,
                12L,
                "ADM-101",
                AdmissionStatus.TRAINING_STARTED,
                "Tailoring",
                "Meena"
        ))));

        ApplicationForm fallbackCandidate = buildCandidate(102L, "Zoya Bano");
        fallbackCandidate.setPreferredExperienceTrack(PreferredExperienceTrack.HANDICRAFT);
        fallbackCandidate.setAdmissions(new ArrayList<>());

        when(applicationFormRepository.findAll()).thenReturn(List.of(fallbackCandidate, assignedCandidate));

        mockMvc.perform(get("/api/v1/candidates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].candidateId").value(101))
                .andExpect(jsonPath("$[0].name").value("Asha Devi"))
                .andExpect(jsonPath("$[0].mobile").value("9876543210"))
                .andExpect(jsonPath("$[0].status").value("TRAINING_STARTED"))
                .andExpect(jsonPath("$[0].courseName").value("Tailoring"))
                .andExpect(jsonPath("$[0].createdDate").value("2026-04-08"))
                .andExpect(jsonPath("$[1].candidateId").value(102))
                .andExpect(jsonPath("$[1].status").value("NEW"))
                .andExpect(jsonPath("$[1].courseName").value("Handicraft"));
    }

    @Test
    void getCandidateByIdReturnsProfileContract() throws Exception {
        ApplicationForm candidate = buildCandidate(101L, "Asha Devi");
        candidate.setHandicraftExperience(Status.YES);
        candidate.setAdmissions(new ArrayList<>(List.of(buildAdmission(
                candidate,
                12L,
                "ADM-101",
                AdmissionStatus.TRAINING_STARTED,
                "Tailoring",
                "Meena"
        ))));

        when(applicationFormRepository.findById(101L)).thenReturn(Optional.of(candidate));

        mockMvc.perform(get("/api/v1/candidates/{id}", 101L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101))
                .andExpect(jsonPath("$.fullName").value("Asha Devi"))
                .andExpect(jsonPath("$.mobileNumber").value("9876543210"))
                .andExpect(jsonPath("$.createdDate").value("2026-04-08"))
                .andExpect(jsonPath("$.educationLevel").value("SECONDARY"))
                .andExpect(jsonPath("$.HandicraftExperience").value("YES"))
                .andExpect(jsonPath("$.admissions[0].admissionNumber").value("ADM-101"))
                .andExpect(jsonPath("$.admissions[0].status").value("TRAINING_STARTED"))
                .andExpect(jsonPath("$.admissions[0].batch.id").value(12))
                .andExpect(jsonPath("$.admissions[0].batch.course.courseName").value("Tailoring"))
                .andExpect(jsonPath("$.admissions[0].batch.trainer.name").value("Meena"));
    }

    @Test
    void getCandidatesByBatchReturnsBatchScopedListContract() throws Exception {
        ApplicationForm candidate = buildCandidate(101L, "Asha Devi");
        candidate.setAdmissions(new ArrayList<>(List.of(buildAdmission(
                candidate,
                12L,
                "ADM-101",
                AdmissionStatus.TRAINING_STARTED,
                "Tailoring",
                "Meena"
        ))));

        when(batchRepository.existsById(12L)).thenReturn(true);
        when(applicationFormRepository.findApplicationFormIdsByBatchId(12L)).thenReturn(List.of(101L));
        when(applicationFormRepository.findById(101L)).thenReturn(Optional.of(candidate));

        mockMvc.perform(get("/api/v1/batches/{batchId}/candidates", 12L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].candidateId").value(101))
                .andExpect(jsonPath("$[0].name").value("Asha Devi"))
                .andExpect(jsonPath("$[0].status").value("TRAINING_STARTED"))
                .andExpect(jsonPath("$[0].courseName").value("Tailoring"));
    }

    private static ApplicationForm buildCandidate(Long id, String fullName) {
        ApplicationForm candidate = new ApplicationForm();
        candidate.setId(id);
        candidate.setFullName(fullName);
        candidate.setCreatedDate(LocalDate.of(2026, 4, 8));
        candidate.setFatherOrHusbandName("Ramesh Devi");
        candidate.setMobileNumber("9876543210");
        candidate.setFullAddress("Garhi, Banswara");
        candidate.setEducationLevel(EducationLevel.SECONDARY);
        candidate.setCasteCategory(CasteCategory.OBC);
        candidate.setPrimarySourceOfIncome("Stitching");
        candidate.setPreviousSkillTraining("Basic tailoring");
        candidate.setAdmissions(new ArrayList<>());
        return candidate;
    }

    private static Admission buildAdmission(
            ApplicationForm candidate,
            Long batchId,
            String admissionNumber,
            AdmissionStatus status,
            String courseName,
            String trainerName
    ) {
        Course course = Course.builder()
                .courseId(7L)
                .courseName(courseName)
                .build();

        Trainer trainer = new Trainer();
        trainer.setTrainerId(9L);
        trainer.setName(trainerName);

        Batch batch = Batch.builder()
                .id(batchId)
                .batchName("APR-2026-A")
                .timing("10:00 AM - 12:00 PM")
                .course(course)
                .trainer(trainer)
                .startDate(LocalDate.of(2026, 4, 1))
                .endDate(LocalDate.of(2026, 6, 30))
                .capacity(30)
                .build();

        Admission admission = new Admission();
        admission.setAdmissionNumber(admissionNumber);
        admission.setStatus(status);
        admission.setCandidate(candidate);
        admission.setBatch(batch);
        return admission;
    }
}