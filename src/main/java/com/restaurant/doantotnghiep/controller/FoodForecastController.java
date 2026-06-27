package com.restaurant.doantotnghiep.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.doantotnghiep.dto.FoodForecastDTO;
import com.restaurant.doantotnghiep.service.FoodForecastService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/food-forecast")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FoodForecastController {

    private final FoodForecastService forecastService;

    @GetMapping
    public ResponseEntity<List<FoodForecastDTO>> getForecast(
            @RequestParam(defaultValue = "WEEK") String mode,
            @RequestParam(required = false) Long branchId,
            @RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(forecastService.getForecast(mode, branchId, topN));
    }
}
