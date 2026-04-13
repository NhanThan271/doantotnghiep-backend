package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.RecipeRequest;
import com.restaurant.doantotnghiep.entity.Recipe;

import java.util.List;

public interface RecipeService {

    List<Recipe> createMany(RecipeRequest request);

    List<Recipe> updateMany(Long foodId, List<RecipeRequest.IngredientItem> items);

    Recipe updateOne(Long id, Double quantityRequired);

    void delete(Long id);

    Recipe getById(Long id);

    List<Recipe> getAll();

    List<Recipe> getByFood(Long foodId);

    List<Recipe> getByIngredient(Long ingredientId);
}
