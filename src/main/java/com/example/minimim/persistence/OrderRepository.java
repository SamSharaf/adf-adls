package com.example.minimim.persistence;

import com.example.minimim.domain.OrderRecord;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderRecord, String> {
    List<OrderRecord> findAllByOrderedAtAfterOrderByOrderedAtAsc(Instant orderedAfter);
}
