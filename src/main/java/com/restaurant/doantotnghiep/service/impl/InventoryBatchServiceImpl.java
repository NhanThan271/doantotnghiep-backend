package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.InventoryBatchService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryBatchServiceImpl implements InventoryBatchService {

    private final InventoryBatchRepository batchRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchIngredientRepository branchIngredientRepository;

    public InventoryBatchServiceImpl(InventoryBatchRepository batchRepository,
            BranchRepository branchRepository,
            IngredientRepository ingredientRepository,
            BranchIngredientRepository branchIngredientRepository) {
        this.batchRepository = batchRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
        this.branchIngredientRepository = branchIngredientRepository;
    }

    @Override
    public InventoryBatch create(Long branchId, Long ingredientId,
            Double quantity, LocalDate expiryDate) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        // tạo batch
        InventoryBatch batch = InventoryBatch.builder()
                .branch(branch)
                .ingredient(ingredient)
                .quantity(quantity)
                .remainingQuantity(quantity)
                .expiryDate(expiryDate)
                .importedAt(LocalDateTime.now())
                .build();

        batchRepository.save(batch);

        // update tồn tổng chi nhánh - nguyên liệu
        BranchIngredient branchIngredient = branchIngredientRepository
                .findByBranchIdAndIngredientId(branchId, ingredientId)
                .orElse(null);

        if (branchIngredient == null) {
            branchIngredient = BranchIngredient.builder()
                    .branch(branch)
                    .ingredient(ingredient)
                    .quantity(quantity)
                    .build();
        } else {
            branchIngredient.setQuantity(
                    branchIngredient.getQuantity() + quantity);
        }

        branchIngredientRepository.save(branchIngredient);

        return batch;
    }

    @Override
    public InventoryBatch getById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    @Override
    public List<InventoryBatch> getAll() {
        return batchRepository.findAll();
    }

    @Override
    public List<InventoryBatch> getByBranchAndIngredient(Long branchId, Long ingredientId) {
        return batchRepository
                .findByBranchIdAndIngredientIdOrderByImportedAtAsc(branchId, ingredientId);
    }

    @Override
    public void delete(Long id) {
        InventoryBatch batch = getById(id);
        batchRepository.delete(batch);
    }
}