package com.example.minimim.api.dto;

import com.example.minimim.domain.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

public record CreateOrderRequest(
        @NotBlank String customerId,
        @NotBlank String productCode,
        @NotNull @Min(1) Integer quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @NotNull OrderStatus orderStatus,
        Instant orderedAt
) {
}
