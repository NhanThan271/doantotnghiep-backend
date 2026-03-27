package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Ingredient;

import java.util.List;

public interface IngredientService {

    Ingredient create(Ingredient ingredient);

    Ingredient update(Long id, Ingredient ingredient);

    void delete(Long id);

    Ingredient getById(Long id);

    List<Ingredient> getAll();

    List<Ingredient> getAllActive();

    List<Ingredient> search(String keyword);
}
