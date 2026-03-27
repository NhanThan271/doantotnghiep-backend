package com.restaurant.doantotnghiep.dto;

import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderStatusDTO {
    private Long id;
    private String customerName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private TableDTO table;
    private PromotionDTO promotion;
    private List<OrderItemDTO> items;

    @Data
    @Builder
    public static class OrderItemDTO {
        private Long id;
        private Long foodId;
        private String foodName;
        private String foodImageUrl;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    public static class TableDTO {
        private Long id;
        private Integer number;
        private String status;
    }

    @Data
    @Builder
    public static class PromotionDTO {
        private Long id;
        private String name;
        private BigDecimal discountPercentage;
        private BigDecimal discountAmount;
    }
}