package com.example.minimim.service;

import com.example.minimim.api.dto.CreateOrderRequest;
import com.example.minimim.api.dto.OrderResponse;
import com.example.minimim.domain.OrderRecord;
import com.example.minimim.persistence.OrderRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        OrderRecord order = new OrderRecord();
        order.setCustomerId(request.customerId().trim());
        order.setProductCode(request.productCode().trim());
        order.setQuantity(request.quantity());
        order.setUnitPrice(request.unitPrice().setScale(2, RoundingMode.HALF_UP));
        order.setOrderStatus(request.orderStatus());
        order.setOrderedAt(request.orderedAt() == null ? Instant.now() : request.orderedAt());

        OrderRecord saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(Instant orderedAfter, int limit) {
        List<OrderRecord> source;
        if (orderedAfter == null) {
            source = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderedAt"));
        } else {
            source = orderRepository.findAllByOrderedAtAfterOrderByOrderedAtAsc(orderedAfter);
        }

        return source.stream()
                .limit(limit)
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<OrderResponse> findById(String id) {
        return orderRepository.findById(id).map(this::toResponse);
    }

    OrderResponse toResponse(OrderRecord order) {
        BigDecimal lineTotal = order.getUnitPrice().multiply(BigDecimal.valueOf(order.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getProductCode(),
                order.getQuantity(),
                order.getUnitPrice(),
                lineTotal,
                order.getOrderStatus(),
                order.getOrderedAt(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
