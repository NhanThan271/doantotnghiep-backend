package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Food;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface FoodService {
    List<Food> getAllFoods();

    Food getFoodById(Long id);

    List<Food> getFoodsByCategory(Long categoryId);

    Food createFood(String name, BigDecimal price, Long categoryId, MultipartFile image);

    Food updateFood(Long id, String name, BigDecimal price, Long categoryId,
            MultipartFile image);

    void deleteFood(Long id);

    List<Food> searchFoods(String keyword);
}