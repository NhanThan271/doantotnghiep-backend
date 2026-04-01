package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Ingredient;
import com.restaurant.doantotnghiep.dto.RecipeRequest;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.Recipe;
import com.restaurant.doantotnghiep.repository.IngredientRepository;
import com.restaurant.doantotnghiep.repository.FoodRepository;
import com.restaurant.doantotnghiep.repository.RecipeRepository;
import com.restaurant.doantotnghiep.service.RecipeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Recipe> createMany(RecipeRequest request) {

        Food food = foodRepository.findById(request.getFoodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        List<Recipe> recipes = new ArrayList<>();

        for (RecipeRequest.IngredientItem item : request.getIngredients()) {

            // check trùng công thức
            recipeRepository.findByFoodIdAndIngredientId(
                    request.getFoodId(),
                    item.getIngredientId()).ifPresent(r -> {
                        throw new RuntimeException("Ingredient already exists in recipe");
                    });

            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));

            Recipe recipe = Recipe.builder()
                    .food(food)
                    .ingredient(ingredient)
                    .quantityRequired(item.getQuantityRequired())
                    .build();

            recipes.add(recipe);
        }

        return recipeRepository.saveAll(recipes);
    }

    @Override
    public List<Recipe> updateMany(Long foodId, List<RecipeRequest.IngredientItem> items) {

        List<Recipe> oldRecipes = recipeRepository.findByFoodId(foodId);
        recipeRepository.deleteAll(oldRecipes);

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        List<Recipe> newRecipes = new ArrayList<>();

        for (RecipeRequest.IngredientItem item : items) {

            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));

            Recipe recipe = Recipe.builder()
                    .food(food)
                    .ingredient(ingredient)
                    .quantityRequired(item.getQuantityRequired())
                    .build();

            newRecipes.add(recipe);
        }

        return recipeRepository.saveAll(newRecipes);
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
