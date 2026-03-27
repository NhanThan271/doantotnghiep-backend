package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByIsActiveTrue();

    List<Ingredient> findByNameContainingIgnoreCase(String keyword);
}
