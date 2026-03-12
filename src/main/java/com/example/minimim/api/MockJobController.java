package com.example.minimim.api;

import com.example.minimim.api.dto.IngestionJobResponse;
import com.example.minimim.api.dto.StartMockLoadRequest;
import com.example.minimim.service.MockIngestionJobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/jobs")
public class MockJobController {

    private final MockIngestionJobService mockIngestionJobService;

    public MockJobController(MockIngestionJobService mockIngestionJobService) {
        this.mockIngestionJobService = mockIngestionJobService;
    }

    @PostMapping("/mock-orders")
    public ResponseEntity<IngestionJobResponse> startMockOrders(@Valid @RequestBody StartMockLoadRequest request) {
        IngestionJobResponse response = mockIngestionJobService.startMockOrderLoad(request.orderCount());
        return ResponseEntity.status(ACCEPTED).body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<IngestionJobResponse> getJob(@PathVariable String jobId) {
        return mockIngestionJobService.findById(jobId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Job not found"));
    }
}
