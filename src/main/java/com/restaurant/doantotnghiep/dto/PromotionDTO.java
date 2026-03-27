package com.restaurant.doantotnghiep.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class PromotionDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<Long> foodIds;
    private List<Long> branchIds;

    // Để trả về frontend (optional)
    private List<FoodDTO> foods;
}
