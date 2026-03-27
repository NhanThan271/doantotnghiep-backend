package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe create(Long foodId, Long ingredientId, Double quantityRequired);

    Recipe update(Long id, Double quantityRequired);

    void delete(Long id);

    Recipe getById(Long id);

    List<Recipe> getAll();

    List<Recipe> getByFood(Long foodId);

    List<Recipe> getByIngredient(Long ingredientId);
}
