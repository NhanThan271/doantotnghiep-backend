package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.BranchIngredient;

import java.util.List;

public interface BranchIngredientService {

    List<BranchIngredient> getAll();

    List<BranchIngredient> getByBranch(Long branchId);

    BranchIngredient getById(Long id);

    BranchIngredient create(Long branchId, Long ingredientId, Double quantity);

    BranchIngredient updateQuantity(Long id, Double quantity);

    void delete(Long id);

    void deductIngredient(Long branchId, Long ingredientId, Double quantityUsed);
}
