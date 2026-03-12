package com.example.minimim.api.dto;

import com.example.minimim.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        String id,
        String customerId,
        String productCode,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        OrderStatus orderStatus,
        Instant orderedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
