package com.restaurant.doantotnghiep.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientPreparationDTO {
    private Long ingredientId;
    private String ingredientName;
    private String unit;

    private Double totalRequired; // tổng cần dùng
    private Double currentStock; // tồn kho (BranchIngredient)
    private Double shortage; // thiếu = max(0, totalRequired - currentStock)

    private List<BatchInfo> batches; // chi tiết theo lô

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BatchInfo {
        private Long batchId;
        private Double remainingQuantity;
        private java.time.LocalDate expiryDate;
        private java.time.LocalDateTime importedAt;
    }
}