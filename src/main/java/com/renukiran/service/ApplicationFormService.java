package com.renukiran.service;

import com.renukiran.dto.BaseResponse;
import com.renukiran.entity.APIStatus;
import com.renukiran.entity.ApplicationForm;
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
        List<ApplicationForm> forms = repository.findAll();
        forms.stream().forEach(ApplicationFormService::removeNestedObjects);
        return forms;
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
        formData.getAdmissions().stream().forEach(admission -> {
            admission.setCandidate(null);
            admission.getBatch().getCourse().getBatchesList().stream().forEach(batch -> {
               batch.setCourse(null);
           });
        });
    }

    public BaseResponse<ApplicationForm> update(Long id, ApplicationForm updated) {

        BaseResponse<ApplicationForm> response = new BaseResponse<>();

        Optional<ApplicationForm> existing = repository.findById(id);
        if (existing.isPresent()) {
            response.setStatus(APIStatus.SUCCESS);
            BeanUtils.copyProperties(updated, existing, "id");
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
}
