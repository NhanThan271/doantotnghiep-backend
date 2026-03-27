package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByFoodId(Long foodId);

    List<Recipe> findByIngredientId(Long ingredientId);

    Optional<Recipe> findByFoodIdAndIngredientId(Long foodId, Long ingredientId);
}
