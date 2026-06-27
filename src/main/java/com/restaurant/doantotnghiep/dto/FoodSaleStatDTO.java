package com.restaurant.doantotnghiep.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodSaleStatDTO {
    private Long foodId;
    private String foodName;
    private Long totalSold;
    private Integer period;
    private Integer year;
}
