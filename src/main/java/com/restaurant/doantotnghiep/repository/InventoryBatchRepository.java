package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

        List<InventoryBatch> findByBranchIdAndIngredientIdOrderByImportedAtAsc(
                        Long branchId, Long ingredientId);

        List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanOrderByImportedAtAsc(
                        Long branchId, Long ingredientId, Double quantity);

        void deleteByWarehouseId(Long warehouseId);

}