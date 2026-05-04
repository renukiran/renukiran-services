package com.renukiran.service;

import com.renukiran.dto.BatchRequest;
import com.renukiran.dto.BatchResponse;
import com.renukiran.entity.Batch;


import com.renukiran.entity.Users;
import com.renukiran.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import com.renukiran.entity.Course;
import com.renukiran.exception.BusinessValidationException;
import com.renukiran.exception.DuplicateResourceException;
import com.renukiran.exception.ResourceNotFoundException;
import com.renukiran.repository.BatchRepository;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final SignUpRepository userRepository;
    private final ApplicationFormRepository applicationFormRepository;
    private final com.renukiran.repository.CandidateAttendanceRepository candidateAttendanceRepository;

/*

    public List<BatchResponse> getAllBatches() {
        return batchRepository.findAll().stream()
                .map(BatchResponse::from)
                .toList();
    }

    public BatchResponse getBatchByCode(String batchCode) {
        Batch batch = batchRepository.findByBatchCode(batchCode)
                .orElseThrow(() -> new RuntimeException("Batch not found: " + batchCode));
        return BatchResponse.from(batch);
    }

    public BatchResponse getBatchById(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + id));
        return BatchResponse.from(batch);
    }

    public BatchResponse createBatch(BatchRequest request) {
        if (batchRepository.existsByBatchCode(request.batchCode().toUpperCase())) {
            throw new RuntimeException("Batch code already exists: " + request.batchCode());
        }
        Batch batch = Batch.builder()
                .batchCode(request.batchCode().toUpperCase())
                .course(request.course())
                .trainer(request.trainer())
                .location(request.location())
                .dates(request.dates())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .enrolled(request.enrolled() != null ? request.enrolled() : 0)
                .max(request.max() != null ? request.max() : 20)
                .status(request.status() != null ? request.status() : "Upcoming")
                .notes(request.notes())
                .build();
        return BatchResponse.from(batchRepository.save(batch));
    }

    public BatchResponse updateBatch(Long id, BatchRequest request) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + id));
        if (request.course() != null)   batch.setCourse(request.course());
        if (request.trainer() != null)  batch.setTrainer(request.trainer());
        if (request.location() != null) batch.setLocation(request.location());
        if (request.dates() != null)    batch.setDates(request.dates());
        if (request.startDate() != null) batch.setStartDate(request.startDate());
        if (request.endDate() != null)  batch.setEndDate(request.endDate());
        if (request.enrolled() != null) batch.setEnrolled(request.enrolled());
        if (request.max() != null)      batch.setMax(request.max());
        if (request.status() != null)   batch.setStatus(request.status());
        if (request.notes() != null)    batch.setNotes(request.notes());
        return BatchResponse.from(batchRepository.save(batch));
    }

    public void deleteBatch(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new RuntimeException("Batch not found with id: " + id);
        }
        batchRepository.deleteById(id);
    }

*/



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

        Users trainer = userRepository.findById(request.getTrainerId())
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

        Users trainer = userRepository.findById(request.getTrainerId())
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

    private void mapToEntity(Batch batch, BatchRequest req, Course course, Users trainer) {
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
        response.setTrainerId(batch.getTrainer().getId());
        response.setTrainerName(batch.getTrainer().getFirstName()+" "+batch.getTrainer().getLastName());
        response.setCapacity(batch.getCapacity());
        response.setEndDate(batch.getEndDate());
        response.setStartDate(batch.getStartDate());
        List<Long> candidateIds = applicationFormRepository.findApplicationFormIdsByBatchId(batch.getId());
        response.setCandidates(candidateIds);

        // build candidatesWithAttendance using candidate ids and attendance repository
        List<com.renukiran.dto.CandidateResponse> candidatesWithAttendance = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate batchStart = batch.getStartDate();
        LocalDate batchEnd = batch.getEndDate();
        LocalDate effectiveEnd = (batchEnd == null) ? null : (batchEnd.isAfter(today) ? today : batchEnd);
        int totalDays = 0;
        if (batchStart != null && effectiveEnd != null && !effectiveEnd.isBefore(batchStart)) {
            totalDays = (int) ChronoUnit.DAYS.between(batchStart, effectiveEnd) + 1;
        }

        for (Long cid : candidateIds) {
            Optional<com.renukiran.entity.ApplicationForm> maybeAf = applicationFormRepository.findById(cid);
            if (maybeAf.isEmpty()) continue;
            com.renukiran.entity.ApplicationForm af = maybeAf.get();

            // --- New: compute per-admission attendancePercentage for this application form ---
            LocalDate now = LocalDate.now();
            if (af.getAdmissions() != null) {
                for (com.renukiran.entity.Admission adm : af.getAdmissions()) {
                    if (adm == null || adm.getBatch() == null) continue;
                    try {
                        Long admBatchId = adm.getBatch().getId();
                        if (admBatchId == null) {
                            adm.setAttendancePercentage(null);
                            continue;
                        }

                        LocalDate admStart = adm.getBatch().getStartDate();
                        LocalDate admEnd = adm.getBatch().getEndDate();
                        LocalDate admEffectiveEnd = (admEnd == null) ? null : (admEnd.isAfter(now) ? now : admEnd);

                        int admTotalDays = 0;
                        if (admStart != null && admEffectiveEnd != null && !admEffectiveEnd.isBefore(admStart)) {
                            admTotalDays = (int) ChronoUnit.DAYS.between(admStart, admEffectiveEnd) + 1;
                        }

                        Integer admPercentage = null;
                        if (admTotalDays > 0) {
                            long admPresentCount = candidateAttendanceRepository
                                    .countByBatch_IdAndCandidate_IdAndAttendanceStatus(admBatchId, af.getId(), com.renukiran.enums.CandidateAttendanceStatus.PRESENT);
                            admPercentage = (int) Math.round((admPresentCount * 100.0) / admTotalDays);
                        }

                        adm.setAttendancePercentage(admPercentage);
                    } catch (Exception e) {
                        // if any error, set null and continue
                        try { adm.setAttendancePercentage(null); } catch (Exception ignore) {}
                    }
                }
            }
            // --- end per-admission computation ---

            Integer percentage = null;
            if (totalDays > 0) {
                long presentCount = candidateAttendanceRepository
                        .countByBatch_IdAndCandidate_IdAndAttendanceStatus(batch.getId(), af.getId(), com.renukiran.enums.CandidateAttendanceStatus.PRESENT);
                percentage = (int) Math.round((presentCount * 100.0) / totalDays);
            }
            candidatesWithAttendance.add(com.renukiran.dto.CandidateResponse.from(af, percentage));
        }

        response.setCandidatesWithAttendance(candidatesWithAttendance);
       return response;
    }
}
