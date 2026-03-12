package com.example.minimim.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StartMockLoadRequest(
        @NotNull @Min(1) @Max(5000) Integer orderCount
) {
}
