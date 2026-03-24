package com.renukiran.controllers;

import com.renukiran.dto.BaseResponse;
import com.renukiran.entity.APIStatus;
import com.renukiran.entity.ApplicationForm;
import com.renukiran.service.ApplicationFormService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applicationForm")
public class ApplicationFormController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationFormController.class);

    @Autowired
    private ApplicationFormService service;

    @PostMapping
    public BaseResponse<ApplicationForm> create(@Valid @RequestBody ApplicationForm form) {
        log.info("Received create ApplicationForm request: {}", form);
        try {
            ApplicationForm saved = service.create(form);
            log.info("ApplicationForm created with id: {}", saved.getId());
            BaseResponse<ApplicationForm> response = new BaseResponse<>();
            response.setData(saved);
            return response;
        } catch (Exception e) {
            log.error("Error while creating ApplicationForm", e);
            throw e;
        }
    }

    @GetMapping
    public BaseResponse<List<ApplicationForm>> list() {
        BaseResponse<List<ApplicationForm>> response = new BaseResponse<>();
        response.setStatus(APIStatus.SUCCESS);
        response.setData(service.listAll());
        return response;
    }

    @GetMapping("/{id}")
    public BaseResponse<ApplicationForm> get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public BaseResponse<ApplicationForm> update(@PathVariable Long id, @RequestBody ApplicationForm form) {
        return service.update(id, form);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<String> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
