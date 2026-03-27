package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Ingredient;
import com.restaurant.doantotnghiep.repository.IngredientRepository;
import com.restaurant.doantotnghiep.service.IngredientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Ingredient create(Ingredient ingredient) {
        ingredient.setIsActive(true);
        return ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient update(Long id, Ingredient ingredient) {
        Ingredient existing = getById(id);
        existing.setName(ingredient.getName());
        existing.setUnit(ingredient.getUnit());
        return ingredientRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Ingredient ingredient = getById(id);
        ingredient.setIsActive(false); // soft delete
        ingredientRepository.save(ingredient);
    }

    @Override
    public Ingredient getById(Long id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
    }

    @Override
    public List<Ingredient> getAll() {
        return ingredientRepository.findAll();
    }

    @Override
    public List<Ingredient> getAllActive() {
        return ingredientRepository.findByIsActiveTrue();
    }

    @Override
    public List<Ingredient> search(String keyword) {
        return ingredientRepository.findByNameContainingIgnoreCase(keyword);
    }
}
