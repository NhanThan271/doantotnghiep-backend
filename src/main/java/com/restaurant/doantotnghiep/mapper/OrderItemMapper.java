package com.restaurant.doantotnghiep.mapper;

import com.restaurant.doantotnghiep.dto.OrderItemResponse;
import com.restaurant.doantotnghiep.entity.OrderItem;

public class OrderItemMapper {

    public static OrderItemResponse toResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getFood().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal());
    }
}
