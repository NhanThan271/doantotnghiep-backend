package com.restaurant.doantotnghiep.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBatchDTO {
    private Long id;
    private String ingredientName;
    private String unit;
    private Double quantity;
    private Double remainingQuantity;
    private String type;
    private String createdBy;
    private LocalDateTime createdAt;
    private String warehouseName;
    private Long requestId;
    private String note;
    private Long daysToExpire;
    private boolean expired;
    private boolean nearExpired;
}