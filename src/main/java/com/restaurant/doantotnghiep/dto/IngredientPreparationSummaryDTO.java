package com.restaurant.doantotnghiep.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientPreparationSummaryDTO {
    private Long branchId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int totalReservations; // số đơn trong khoảng
    private List<IngredientPreparationDTO> ingredients;
}