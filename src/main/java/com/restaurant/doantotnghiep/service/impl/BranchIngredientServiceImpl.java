package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.BranchIngredient;
import com.restaurant.doantotnghiep.entity.Ingredient;
import com.restaurant.doantotnghiep.repository.BranchIngredientRepository;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.IngredientRepository;
import com.restaurant.doantotnghiep.service.BranchIngredientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BranchIngredientServiceImpl implements BranchIngredientService {

    private final BranchIngredientRepository branchIngredientRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;

    public BranchIngredientServiceImpl(
            BranchIngredientRepository branchIngredientRepository,
            BranchRepository branchRepository,
            IngredientRepository ingredientRepository) {
        this.branchIngredientRepository = branchIngredientRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public List<BranchIngredient> getAll() {
        return branchIngredientRepository.findAll();
    }

    @Override
    public List<BranchIngredient> getByBranch(Long branchId) {
        return branchIngredientRepository.findByBranchId(branchId);
    }

    @Override
    public BranchIngredient getById(Long id) {
        return branchIngredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BranchIngredient not found"));
    }

    @Override
    public BranchIngredient create(Long branchId, Long ingredientId, Double quantity) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        branchIngredientRepository.findByBranchIdAndIngredientId(branchId, ingredientId)
                .ifPresent(bi -> {
                    throw new RuntimeException("Ingredient already exists in this branch");
                });

        BranchIngredient bi = BranchIngredient.builder()
                .branch(branch)
                .ingredient(ingredient)
                .quantity(quantity)
                .build();

        return branchIngredientRepository.save(bi);
    }

    @Override
    public BranchIngredient updateQuantity(Long id, Double quantity) {
        BranchIngredient bi = getById(id);
        bi.setQuantity(quantity);
        return branchIngredientRepository.save(bi);
    }

    @Override
    public void delete(Long id) {
        branchIngredientRepository.deleteById(id);
    }

    @Override
    public void deductIngredient(Long branchId, Long ingredientId, Double quantityUsed) {

        BranchIngredient bi = branchIngredientRepository
                .findByBranchIdAndIngredientId(branchId, ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found in branch"));

        if (bi.getQuantity() < quantityUsed) {
            throw new RuntimeException("Not enough ingredient in stock");
        }

        bi.setQuantity(bi.getQuantity() - quantityUsed);
        branchIngredientRepository.save(bi);
    }
}
