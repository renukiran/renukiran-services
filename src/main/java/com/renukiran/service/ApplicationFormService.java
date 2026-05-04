package com.renukiran.service;

import com.renukiran.dto.ApplicationStatsResponse;
import com.renukiran.dto.BaseResponse;
import com.renukiran.entity.APIStatus;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Admission;
import com.renukiran.enums.AdmissionStatus;
import com.renukiran.repository.ApplicationFormRepository;
import com.renukiran.repository.CandidateAttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ApplicationFormService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationFormService.class);

    private final ApplicationFormRepository repository;

    private final AdmissionService admissionService;

    private final CandidateAttendanceRepository candidateAttendanceRepository;

    public ApplicationForm create(ApplicationForm form) {
        log.info("Creating ApplicationForm: {}", form);
        try {

            if (form.getCreatedDate() == null) {
                form.setCreatedDate(LocalDate.now());
            }
            ApplicationForm saved = repository.save(form);
            log.info("Saved ApplicationForm id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Error saving ApplicationForm", e);
            throw e;
        }
    }

    public List<ApplicationForm> listAll() {
        // First update statuses for all application forms based on their batch dates
        List<ApplicationForm> forms = repository.findAll();
        for (ApplicationForm form : forms) {
            if (form == null || form.getId() == null) continue;
            try {
                // call the public method and ignore its response here
                updateStatusFromBatchDates(form.getId());
            } catch (Exception e) {
                log.warn("Failed to update status from batch dates for application id={}", form.getId(), e);
            }
        }

        // Re-fetch the list to reflect any persisted status changes
        List<ApplicationForm> updatedForms = repository.findAll();

        // compute attendance percentage for each application
        for (ApplicationForm af : updatedForms) {
            try {
              calculateAttendancePercentageForApplication(af);

            } catch (Exception e) {
                log.warn("Failed to calculate attendance percentage for application id={}", af.getId(), e);
            }
        }

        updatedForms.forEach(ApplicationFormService::removeNestedObjects);
        return updatedForms;
    }

    /**
     * Calculate attendance percentage for an application form based on its admissions.
     * If the application has multiple admissions, take the maximum percentage among them.
     * If no admissions or batch dates missing, return null.
     */
    private void calculateAttendancePercentageForApplication(ApplicationForm af) {
        if (af == null || af.getAdmissions() == null || af.getAdmissions().isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();

        for (Admission admission : af.getAdmissions()) {
            if (admission == null || admission.getBatch() == null) continue;
            try {
                Long batchId = admission.getBatch().getId();
                if (batchId == null) continue;
                LocalDate start = admission.getBatch().getStartDate();
                LocalDate end = admission.getBatch().getEndDate();

                // Cap the end date to today: we don't count future dates
                LocalDate effectiveEnd = end == null ? null : (end.isAfter(today) ? today : end);

                int totalDays = 0;
                if (start != null && effectiveEnd != null && !effectiveEnd.isBefore(start)) {
                    totalDays = (int) ChronoUnit.DAYS.between(start, effectiveEnd) + 1;
                }

                Integer percentage = null;
                if (totalDays > 0) {
                    long presentCount = candidateAttendanceRepository
                            .countByBatch_IdAndCandidate_IdAndAttendanceStatus(batchId, af.getId(), com.renukiran.enums.CandidateAttendanceStatus.PRESENT);
                    percentage = (int) Math.round((presentCount * 100.0) / totalDays);
                }

                admission.setAttendancePercentage(percentage);
            } catch (Exception e) {
                log.warn("Error while calculating percentage for application {}", af.getId(), e);
            }
        }

    }

    public BaseResponse<ApplicationStatsResponse> getStats() {
        List<ApplicationForm> forms = listAll();
        LocalDate today = LocalDate.now();

        long createdToday = forms.stream()
                .filter(form -> today.equals(form.getCreatedDate()))
                .count();

        // Collect all admissions and group by batch id
        java.util.Map<Long, java.util.List<Admission>> admissionsByBatch = new java.util.HashMap<>();
        for (ApplicationForm af : forms) {
            if (af.getAdmissions() == null) continue;
            for (Admission admission : af.getAdmissions()) {
                if (admission == null || admission.getBatch() == null) continue;
                Long batchId = admission.getBatch().getId();
                if (batchId == null) continue;
                admissionsByBatch.computeIfAbsent(batchId, k -> new java.util.ArrayList<>()).add(admission);
            }
        }

        java.util.List<com.renukiran.dto.BatchAttendanceStat> batchStats = new java.util.ArrayList<>();
        int overallSum = 0;
        int overallCount = 0;

        for (java.util.Map.Entry<Long, java.util.List<Admission>> entry : admissionsByBatch.entrySet()) {
            Long batchId = entry.getKey();
            java.util.List<Admission> batchAdmissions = entry.getValue();

            int sum = 0;
            int count = 0;
            String batchName = null;
            for (Admission admission : batchAdmissions) {
                Integer p = admission.getAttendancePercentage();
                if (batchName == null && admission.getBatch() != null) batchName = admission.getBatch().getBatchName();
                if (p != null) {
                    sum += p;
                    count++;
                }
            }

            Integer avg = null;
            if (count > 0) {
                avg = (int) Math.round(sum * 1.0 / count);
                overallSum += sum;
                overallCount += count;
            }

            batchStats.add(com.renukiran.dto.BatchAttendanceStat.builder()
                    .batchId(batchId)
                    .batchName(batchName)
                    .averageAttendancePercentage(avg)
                    .enrolledCount(batchAdmissions.size())
                    .build());
        }

        Integer overallAvg = null;
        if (overallCount > 0) {
            overallAvg = (int) Math.round(overallSum * 1.0 / overallCount);
        }

        BaseResponse<ApplicationStatsResponse> response = new BaseResponse<>();
        response.setStatus(APIStatus.SUCCESS);
        response.setData(ApplicationStatsResponse.builder()
                .totalApplications(forms.size())
                .createdToday(createdToday)
                .draftCount(0)
                .newCount(countNewApplications(forms))
                .underReviewCount(0)
                .selectedCount(0)
                .assignedToBatchCount(countByHighestAdmissionStatus(forms, AdmissionStatus.ASSIGNED_TO_BATCH))
                .trainingCount(countByHighestAdmissionStatus(forms, AdmissionStatus.TRAINING_STARTED))
                .pendingPlacementCount(countByHighestAdmissionStatus(forms, AdmissionStatus.TRAINING_COMPLETED))
                .placedCount(countByHighestAdmissionStatus(forms, AdmissionStatus.PLACED))
                .notPlacedCount(0)
                .batchAttendanceStats(batchStats)
                .overallAverageAttendancePercentage(overallAvg)
                .build());
        return response;
    }

    public BaseResponse<ApplicationForm> getById(Long id) {

        BaseResponse<ApplicationForm> response = new BaseResponse<>();

        Optional<ApplicationForm> form = repository.findById(id);
        if (form.isPresent()) {
            ApplicationForm formData = form.get();
            calculateAttendancePercentageForApplication(formData);
            removeNestedObjects(formData);
            response.setStatus(APIStatus.SUCCESS);
            response.setData(formData);
        } else {
            response.setStatus(APIStatus.FAILURE);
            response.setMessage("ApplicationForm not found with id: " + id);
        }
        return response;
    }

    private static void removeNestedObjects(ApplicationForm formData) {
        if (formData == null) return;
        List<Admission> admissions = formData.getAdmissions();
        if (admissions == null) return;
        for (Admission admission : admissions) {
            if (admission == null) continue;
            // break the back reference to avoid infinite recursion / nested objects
            try {
                admission.setCandidate(null);
            } catch (Exception ignore) {
            }
            try {
                if (admission.getBatch() != null) {
                    if (admission.getBatch().getCourse() != null) {
                        admission.getBatch().getCourse().setBatchesList(null);
                        admission.getBatch().getCourse().setUsers(null);
                    }
                }
            } catch (Exception ignore) {
            }
        }
    }

    public BaseResponse<ApplicationForm> update(Long id, ApplicationForm updated) {

        BaseResponse<ApplicationForm> response = new BaseResponse<>();

        Optional<ApplicationForm> existing = repository.findById(id);
        if (existing.isPresent()) {
            response.setStatus(APIStatus.SUCCESS);
            // copy properties into the actual entity (existing.get()) and preserve id
            BeanUtils.copyProperties(updated, existing.get(), "id");
            response.setData(repository.save(existing.get()));
        } else {
            response.setStatus(APIStatus.FAILURE);
            response.setMessage("ApplicationForm not found with id: " + id);
        }

        return response;
    }

    public BaseResponse<String> delete(Long id) {

        BaseResponse<String> response = new BaseResponse<>();

        if (repository.existsById(id)) {
            repository.deleteById(id);
            response.setStatus(APIStatus.SUCCESS);
        } else {
            response.setStatus(APIStatus.FAILURE);
            response.setMessage("ApplicationForm not found with id: " + id);
        }
        return response;

    }

    public void updateStatusFromBatchDates(Long id) {

        Optional<ApplicationForm> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return;
        }
        ApplicationForm form = existing.get();
        form.getAdmissions().forEach(admission -> {
            try {
                admissionService.updateApplicationStatusBasedOnBatchDates(admission);
            } catch (Exception e) {
                log.warn("Failed to update application status based on batch dates for admission id={}", admission.getAdmissionNumber(), e);
            }
        });

    }

    private long countNewApplications(List<ApplicationForm> forms) {
        return forms.stream()
                .filter(form -> {
                    AdmissionStatus highestStatus = resolveHighestAdmissionStatus(form);
                    return highestStatus == null || highestStatus == AdmissionStatus.NEW;
                })
                .count();
    }

    private long countByHighestAdmissionStatus(List<ApplicationForm> forms, AdmissionStatus status) {
        return forms.stream()
                .filter(form -> resolveHighestAdmissionStatus(form) == status)
                .count();
    }

    private AdmissionStatus resolveHighestAdmissionStatus(ApplicationForm form) {
        if (form == null || form.getAdmissions() == null || form.getAdmissions().isEmpty()) {
            return null;
        }

        return form.getAdmissions().stream()
                .map(Admission::getStatus)
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(Enum::ordinal))
                .orElse(null);
    }


}
