package com.example.minimim.service;

import com.example.minimim.api.dto.CreateOrderRequest;
import com.example.minimim.api.dto.OrderResponse;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderAsyncService {

    private final OrderService orderService;

    public OrderAsyncService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Async("minimimTaskExecutor")
    public CompletableFuture<OrderResponse> createOrderAsync(CreateOrderRequest request) {
        return CompletableFuture.completedFuture(orderService.createOrder(request));
    }
}
