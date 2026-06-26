package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByFoodId(Long foodId);

    List<Recipe> findByIngredientId(Long ingredientId);

    Optional<Recipe> findByFoodIdAndIngredientId(Long foodId, Long ingredientId);

    @Query("SELECT r FROM Recipe r JOIN FETCH r.ingredient WHERE r.food.id IN :foodIds")
    List<Recipe> findByFoodIds(@Param("foodIds") List<Long> foodIds);
}
