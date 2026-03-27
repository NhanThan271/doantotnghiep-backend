package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Ingredient;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.Recipe;
import com.restaurant.doantotnghiep.repository.IngredientRepository;
import com.restaurant.doantotnghiep.repository.FoodRepository;
import com.restaurant.doantotnghiep.repository.RecipeRepository;
import com.restaurant.doantotnghiep.service.RecipeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository,
            FoodRepository foodRepository,
            IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.foodRepository = foodRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Recipe create(Long foodId, Long ingredientId, Double quantityRequired) {

        // Check trùng công thức
        recipeRepository.findByFoodIdAndIngredientId(foodId, ingredientId)
                .ifPresent(r -> {
                    throw new RuntimeException("Recipe already exists for this food and ingredient");
                });

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        Recipe recipe = Recipe.builder()
                .food(food)
                .ingredient(ingredient)
                .quantityRequired(quantityRequired)
                .build();

        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe update(Long id, Double quantityRequired) {
        Recipe recipe = getById(id);
        recipe.setQuantityRequired(quantityRequired);
        return recipeRepository.save(recipe);
    }

    @Override
    public void delete(Long id) {
        Recipe recipe = getById(id);
        recipeRepository.delete(recipe);
    }

    @Override
    public Recipe getById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));
    }

    @Override
    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> getByFood(Long foodId) {
        return recipeRepository.findByFoodId(foodId);
    }

    @Override
    public List<Recipe> getByIngredient(Long ingredientId) {
        return recipeRepository.findByIngredientId(ingredientId);
    }
}
