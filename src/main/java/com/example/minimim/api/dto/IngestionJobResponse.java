package com.example.minimim.api.dto;

import com.example.minimim.domain.JobState;
import java.time.Instant;

public record IngestionJobResponse(
        String id,
        Integer requestedCount,
        Integer processedCount,
        JobState state,
        Instant requestedAt,
        Instant startedAt,
        Instant completedAt,
        String errorMessage,
        Instant updatedAt
) {
}
