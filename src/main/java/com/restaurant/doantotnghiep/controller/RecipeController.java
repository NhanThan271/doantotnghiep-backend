package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.RecipeRequest;
import com.restaurant.doantotnghiep.entity.Recipe;
import com.restaurant.doantotnghiep.service.RecipeService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Recipe> createMany(@RequestBody RecipeRequest request) {
        return recipeService.createMany(request);
    }

    @PutMapping("/food/{foodId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<Recipe> updateMany(
            @PathVariable Long foodId,
            @RequestBody List<RecipeRequest.IngredientItem> items) {
        return recipeService.updateMany(foodId, items);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Recipe updateOne(
            @PathVariable Long id,
            @RequestParam Double quantityRequired) {
        return recipeService.updateOne(id, quantityRequired);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        recipeService.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Recipe getById(@PathVariable Long id) {
        return recipeService.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Recipe> getAll() {
        return recipeService.getAll();
    }

    @GetMapping("/food/{foodId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Recipe> getByFood(@PathVariable Long foodId) {
        return recipeService.getByFood(foodId);
    }

    @GetMapping("/ingredient/{ingredientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<Recipe> getByIngredient(@PathVariable Long ingredientId) {
        return recipeService.getByIngredient(ingredientId);
    }
}
