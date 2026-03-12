package com.example.minimim.api;

import com.example.minimim.api.dto.CreateOrderRequest;
import com.example.minimim.api.dto.OrderResponse;
import com.example.minimim.service.OrderAsyncService;
import com.example.minimim.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final OrderAsyncService orderAsyncService;

    public OrderController(OrderService orderService, OrderAsyncService orderAsyncService) {
        this.orderService = orderService;
        this.orderAsyncService = orderAsyncService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PostMapping("/async")
    public CompletableFuture<ResponseEntity<OrderResponse>> createOrderAsync(@Valid @RequestBody CreateOrderRequest request) {
        return orderAsyncService.createOrderAsync(request).thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return orderService.findById(orderId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DATE_TIME) Instant orderedAfter,
            @RequestParam(defaultValue = "200") @Min(1) @Max(1000) int limit
    ) {
        return ResponseEntity.ok(orderService.listOrders(orderedAfter, limit));
    }
}
