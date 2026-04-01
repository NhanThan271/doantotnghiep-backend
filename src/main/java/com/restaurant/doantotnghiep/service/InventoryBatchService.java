package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.InventoryBatch;

import java.time.LocalDate;
import java.util.List;

public interface InventoryBatchService {

    InventoryBatch create(Long branchId, Long ingredientId,
            Double quantity, LocalDate expiryDate);

    InventoryBatch getById(Long id);

    List<InventoryBatch> getAll();

    List<InventoryBatch> getByBranchAndIngredient(Long branchId, Long ingredientId);

    void delete(Long id);
}