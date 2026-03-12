package com.example.minimim.service;

import com.example.minimim.api.dto.IngestionJobResponse;
import com.example.minimim.domain.IngestionJob;
import com.example.minimim.domain.JobState;
import com.example.minimim.persistence.IngestionJobRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MockIngestionJobService {

    private final IngestionJobRepository ingestionJobRepository;
    private final MockIngestionWorker mockIngestionWorker;

    public MockIngestionJobService(IngestionJobRepository ingestionJobRepository, MockIngestionWorker mockIngestionWorker) {
        this.ingestionJobRepository = ingestionJobRepository;
        this.mockIngestionWorker = mockIngestionWorker;
    }

    public IngestionJobResponse startMockOrderLoad(int orderCount) {
        IngestionJob job = new IngestionJob();
        job.setRequestedCount(orderCount);
        job.setProcessedCount(0);
        job.setState(JobState.PENDING);

        IngestionJob saved = ingestionJobRepository.save(job);
        mockIngestionWorker.process(saved.getId(), orderCount);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Optional<IngestionJobResponse> findById(String id) {
        return ingestionJobRepository.findById(id).map(this::toResponse);
    }

    private IngestionJobResponse toResponse(IngestionJob job) {
        return new IngestionJobResponse(
                job.getId(),
                job.getRequestedCount(),
                job.getProcessedCount(),
                job.getState(),
                job.getRequestedAt(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getErrorMessage(),
                job.getUpdatedAt()
        );
    }
}
