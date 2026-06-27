package com.restaurant.doantotnghiep.service;

import java.util.List;

import com.restaurant.doantotnghiep.dto.FoodForecastDTO;

public interface FoodForecastService {
    List<FoodForecastDTO> getForecast(String mode, Long branchId, int topN);
}
