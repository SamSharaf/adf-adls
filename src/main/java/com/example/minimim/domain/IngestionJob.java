package com.example.minimim.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ingestion_jobs")
public class IngestionJob {

    @Id
    private String id;

    @Column(nullable = false)
    private Integer requestedCount;

    @Column(nullable = false)
    private Integer processedCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobState state;

    @Column(nullable = false)
    private Instant requestedAt;

    @Column
    private Instant startedAt;

    @Column
    private Instant completedAt;

    @Column(length = 2048)
    private String errorMessage;

    @Column(nullable = false)
    private Instant updatedAt;

    public IngestionJob() {
    }

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (requestedAt == null) {
            requestedAt = now;
        }
        updatedAt = now;
        if (processedCount == null) {
            processedCount = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(Integer requestedCount) {
        this.requestedCount = requestedCount;
    }

    public Integer getProcessedCount() {
        return processedCount;
    }

    public void setProcessedCount(Integer processedCount) {
        this.processedCount = processedCount;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
