package com.restaurant.doantotnghiep.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.restaurant.doantotnghiep.entity.enums.OrderStatus;

public record OrderResponse(
                Long id,
                String customerName,
                OrderStatus status,
                BigDecimal totalAmount,
                LocalDateTime createdAt,
                List<OrderItemResponse> items) {
}
