package com.renukiran.service;

import com.renukiran.dto.BatchRequest;
import com.renukiran.dto.BatchResponse;
import com.renukiran.entity.Batch;
import com.renukiran.entity.Course;
import com.renukiran.entity.Trainer;
import com.renukiran.exception.BusinessValidationException;
import com.renukiran.exception.DuplicateResourceException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.BatchRepository;
import com.renukiran.repository.CourseRepository;
import com.renukiran.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final TrainerRepository trainerRepository;

    @Transactional
    public BatchResponse create(BatchRequest request) {

        if (request.getStartDate() != null && request.getEndDate() != null && request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessValidationException("startDate must be before endDate");
        }
        if (batchRepository.existsByBatchName(request.getBatchName())) {
            throw new DuplicateResourceException("Batch already exists with this batch Name:" +request.getBatchName(), "BATCH_ALREADY_EXIST");
        }

        Course course = courseRepository.findById(request.getCourseId())
               .orElseThrow(() -> new ResourceNotFoundException("Course not found", "COURSE_NOT_FOUND"));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found", "TRAINER_NOT_FOUND"));

        Batch batch = new Batch();
        mapToEntity(batch, request, course, trainer);

        return mapToResponse(batchRepository.save(batch));
    }

    public BatchResponse getById(Long id) {

        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found","BATCH_NOT_FOUND"));

        return mapToResponse(batch);
    }

    public Page<BatchResponse> getAll(int page, int size) {

        return batchRepository.findAll(PageRequest.of(page, size))
                .map(this::mapToResponse);
    }

    @Transactional
    public BatchResponse update(Long id, BatchRequest request) {

        Batch batch = batchRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Batch not found","BATCH_NOT_FOUND"));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found","COURSE_NOT_FOUND"));

        Trainer trainer = trainerRepository.findById(request.getTrainerId())
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found","TRAINER_NOT_FOUND"));

        mapToEntity(batch, request, course, trainer);
        return mapToResponse(batchRepository.save(batch));
    }

    @Transactional
    public void delete(Long id) {

        if (!batchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Batch not found","BATCH_NOT_FOUND");
        }

        batchRepository.deleteById(id);
    }

    // 🔁 MAPPERS

    private void mapToEntity(Batch batch, BatchRequest req, Course course, Trainer trainer) {
        batch.setBatchName(req.getBatchName());
        batch.setTiming(req.getTiming());
        batch.setStartDate(req.getStartDate());
        batch.setEndDate(req.getEndDate());
        batch.setCourse(course);
        batch.setTrainer(trainer);
        batch.setCapacity(req.getCapacity());
    }

    private BatchResponse mapToResponse(Batch batch) {
        BatchResponse response = new BatchResponse();
        response.setId(batch.getId());
        response.setBatchName(batch.getBatchName());
        response.setTiming(batch.getTiming());
        response.setCourseId(batch.getCourse().getCourseId());
        response.setCourseName(batch.getCourse().getCourseName());
        response.setTrainerId(batch.getTrainer().getTrainerId());
        response.setTrainerName(batch.getTrainer().getName());
        response.setCapacity(batch.getCapacity());
        response.setEndDate(batch.getEndDate());
        response.setStartDate(batch.getStartDate());
       return response;
    }
}
