package com.restaurant.doantotnghiep.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
                Long id,
                String foodName,
                int quantity,
                BigDecimal price,
                BigDecimal subtotal) {
}
