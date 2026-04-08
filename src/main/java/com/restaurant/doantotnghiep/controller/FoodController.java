package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "http://localhost:3000")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public List<Food> getAllFoods() {
        return foodService.getAllFoods();
    }

    @GetMapping("/{id}")
    public EntityModel<Food> getFoodById(@PathVariable Long id) {

        Food food = foodService.getFoodById(id);

        return EntityModel.of(food,
                linkTo(methodOn(FoodController.class).getFoodById(id)).withSelfRel(),
                linkTo(methodOn(FoodController.class).getAllFoods()).withRel("all-foods"));
    }

    @GetMapping("/search")
    public List<Food> searchFoods(@RequestParam("keyword") String keyword) {
        return foodService.searchFoods(keyword);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Food createFood(
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return foodService.createFood(name, price, categoryId, image);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Food updateFood(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return foodService.updateFood(id, name, price, categoryId, image);
    }

    @GetMapping("/category/{categoryId}")
    public List<Food> getFoodsByCategory(@PathVariable Long categoryId) {
        return foodService.getFoodsByCategory(categoryId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
    }
}
