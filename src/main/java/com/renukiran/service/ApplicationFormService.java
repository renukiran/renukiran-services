package com.renukiran.service;

import com.renukiran.dto.BaseResponse;
import com.renukiran.entity.APIStatus;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.entity.Admission;
import com.renukiran.enums.ApplicationStatus;
import com.renukiran.repository.ApplicationFormRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ApplicationFormService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationFormService.class);

    @Autowired
    private ApplicationFormRepository repository;

    public ApplicationForm create(ApplicationForm form) {
        log.info("Creating ApplicationForm: {}", form);
        try {
            if (form.getApplicationStatus() == null) {
                form.setApplicationStatus(ApplicationStatus.NEW);
            }
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
        updatedForms.forEach(ApplicationFormService::removeNestedObjects);
        return updatedForms;
    }

    public BaseResponse<ApplicationForm> getById(Long id) {

        BaseResponse<ApplicationForm> response = new BaseResponse<>();

        Optional<ApplicationForm> form = repository.findById(id);
        if (form.isPresent()) {
            ApplicationForm formData = form.get();
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
        List<Admission> admissions = form.getAdmissions();
        if (admissions == null || admissions.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        boolean anyActive = false;
        boolean anyUpcoming = false;
        boolean anyEnded = false;

        for (Admission admission : admissions) {
            if (admission == null) continue;
            if (admission.getBatch() == null) continue;
            LocalDate start = admission.getBatch().getStartDate();
            LocalDate end = admission.getBatch().getEndDate();
            if (start == null || end == null) continue;

            if ((start.isEqual(today) || start.isBefore(today)) && (end.isEqual(today) || end.isAfter(today))) { // start <= today <= end
                anyActive = true;
                break; // active training takes precedence
            } else if (end.isBefore(today)) {
                anyEnded = true;
            } else if (start.isAfter(today)) {
                anyUpcoming = true;
            }
        }

        if (anyActive) {
            if (form.getApplicationStatus() != ApplicationStatus.TRAINING_STARTED) {
                form.setApplicationStatus(ApplicationStatus.TRAINING_STARTED);
            }
        } else if (anyEnded && !anyUpcoming) {
            if (form.getApplicationStatus() != ApplicationStatus.TRAINING_COMPLETED) {
                form.setApplicationStatus(ApplicationStatus.TRAINING_COMPLETED);
            }
        }
         repository.save(form);

    }

    public boolean changeStatus(Long id, ApplicationStatus newStatus) {
        Optional<ApplicationForm> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        ApplicationForm form = existing.get();
        ApplicationStatus current = form.getApplicationStatus();
        if (current == newStatus) {
            return false;
        }
        form.setApplicationStatus(newStatus);
        repository.save(form);
        return true;
    }

}
