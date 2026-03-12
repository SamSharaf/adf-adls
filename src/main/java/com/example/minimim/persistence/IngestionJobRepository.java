package com.example.minimim.persistence;

import com.example.minimim.domain.IngestionJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngestionJobRepository extends JpaRepository<IngestionJob, String> {
}
