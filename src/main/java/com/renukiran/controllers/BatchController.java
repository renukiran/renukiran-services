package com.renukiran.controllers;

import com.renukiran.dto.BatchRequest;
import com.renukiran.dto.BatchResponse;
import com.renukiran.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

=======
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

>>>>>>> 7303c45d4cfaa05a7ad6af1e134b47b108598a74
@RestController
@RequestMapping("/api/v1/batches")
@RequiredArgsConstructor
public class BatchController {

<<<<<<< HEAD
    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAllBatches() {
        return ResponseEntity.ok(batchService.getAllBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(batchService.getBatchById(id));
    }

    @PostMapping
    public ResponseEntity<BatchResponse> createBatch(@Valid @RequestBody BatchRequest request) {
        BatchResponse created = batchService.createBatch(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse> updateBatch(@PathVariable Long id,
                                                     @Valid @RequestBody BatchRequest request) {
        return ResponseEntity.ok(batchService.updateBatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }
}
=======
    private final BatchService service;

    @PostMapping
    public ResponseEntity<BatchResponse> create(@Valid @RequestBody BatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BatchResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(service.getAll(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BatchResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BatchRequest request) {

        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
>>>>>>> 7303c45d4cfaa05a7ad6af1e134b47b108598a74
