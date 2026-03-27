package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Category;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.repository.CategoryRepository;
import com.restaurant.doantotnghiep.repository.FoodRepository;
import com.restaurant.doantotnghiep.service.FoodService;
import com.restaurant.doantotnghiep.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StorageService storageService;

    @Override
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    @Override
    public Food getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found with id: " + id));
    }

    @Override
    public List<Food> getFoodsByCategory(Long categoryId) {
        return foodRepository.findByCategoryId(categoryId);
    }

    @Override
    public Food createFood(String name, BigDecimal price, Long categoryId,
            MultipartFile image) {
        Food food = new Food();
        food.setName(name);
        food.setPrice(price);
        food.setCreatedAt(LocalDateTime.now());
        food.setUpdatedAt(LocalDateTime.now());

        // Set category nếu có
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            food.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            food.setImageUrl(imageUrl);
        }

        return foodRepository.save(food);
    }

    @Override
    public Food updateFood(Long id, String name, BigDecimal price, Long categoryId,
            MultipartFile image) {
        Food existing = getFoodById(id);
        existing.setName(name);
        existing.setPrice(price);
        existing.setUpdatedAt(LocalDateTime.now());

        // Update category nếu có
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            existing.setCategory(category);
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = storageService.saveImage(image);
            existing.setImageUrl(imageUrl);
        }

        return foodRepository.save(existing);
    }

    @Override
    public void deleteFood(Long id) {
        Food existing = getFoodById(id);
        foodRepository.delete(existing);
    }

    @Override
    public List<Food> searchFoods(String keyword) {
        return foodRepository.findByNameContainingIgnoreCase(keyword);
    }

}