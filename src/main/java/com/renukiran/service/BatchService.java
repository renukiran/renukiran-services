package com.renukiran.service;

import com.renukiran.dto.BatchRequest;
import com.renukiran.dto.BatchResponse;
import com.renukiran.entity.Batch;
import com.renukiran.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;

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
}
