package com.restaurant.doantotnghiep.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodForecastDTO {
    private Long foodId;
    private String foodName;
    private Double avgPerPeriod; // trung bình mỗi kỳ
    private Long forecastNextPeriod; // dự báo kỳ tới
    private Long totalPast; // tổng đã bán (để rank)
    private String trend; // "UP", "DOWN", "STABLE"
    private List<Long> history; // lịch sử theo kỳ (để vẽ chart)
}
