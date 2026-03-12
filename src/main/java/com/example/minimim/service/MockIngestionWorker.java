package com.example.minimim.service;

import com.example.minimim.domain.IngestionJob;
import com.example.minimim.domain.JobState;
import com.example.minimim.domain.OrderRecord;
import com.example.minimim.domain.OrderStatus;
import com.example.minimim.persistence.IngestionJobRepository;
import com.example.minimim.persistence.OrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MockIngestionWorker {

    private static final List<String> PRODUCTS = List.of(
            "cpu-ryzen-9", "ssd-2tb", "ram-64gb", "gpu-rx-8800", "motherboard-x"
    );

    private static final List<OrderStatus> STATUSES = List.of(
            OrderStatus.CREATED, OrderStatus.PROCESSING, OrderStatus.COMPLETED
    );

    private final IngestionJobRepository ingestionJobRepository;
    private final OrderRepository orderRepository;

    public MockIngestionWorker(IngestionJobRepository ingestionJobRepository, OrderRepository orderRepository) {
        this.ingestionJobRepository = ingestionJobRepository;
        this.orderRepository = orderRepository;
    }

    @Async("minimimTaskExecutor")
    public void process(String jobId, int orderCount) {
        IngestionJob job = ingestionJobRepository.findById(jobId).orElse(null);
        if (job == null) {
            return;
        }

        Random random = new Random();
        job.setState(JobState.RUNNING);
        job.setStartedAt(Instant.now());
        job.setErrorMessage(null);
        ingestionJobRepository.save(job);

        try {
            for (int i = 0; i < orderCount; i++) {
                orderRepository.save(buildMockOrder(random));

                int processed = i + 1;
                if (processed % 25 == 0 || processed == orderCount) {
                    job.setProcessedCount(processed);
                    ingestionJobRepository.save(job);
                }
            }

            job.setState(JobState.COMPLETED);
            job.setProcessedCount(orderCount);
            job.setCompletedAt(Instant.now());
            ingestionJobRepository.save(job);
        } catch (Exception ex) {
            job.setState(JobState.FAILED);
            job.setErrorMessage(ex.getMessage());
            job.setCompletedAt(Instant.now());
            ingestionJobRepository.save(job);
        }
    }

    private OrderRecord buildMockOrder(Random random) {
        OrderRecord order = new OrderRecord();
        order.setCustomerId("cust-" + (1000 + random.nextInt(9000)));
        order.setProductCode(PRODUCTS.get(random.nextInt(PRODUCTS.size())));
        order.setQuantity(1 + random.nextInt(10));
        order.setUnitPrice(BigDecimal.valueOf(49 + random.nextInt(900)).setScale(2, RoundingMode.HALF_UP));
        order.setOrderStatus(STATUSES.get(random.nextInt(STATUSES.size())));
        order.setOrderedAt(Instant.now().minusSeconds(random.nextInt(21_600)));
        return order;
    }
}
