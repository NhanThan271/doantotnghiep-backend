package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.dto.InventoryBatchDTO;
import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

        List<InventoryBatch> findByBranchIdAndIngredientIdOrderByImportedAtAsc(
                        Long branchId, Long ingredientId);

        List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanOrderByImportedAtAsc(
                        Long branchId, Long ingredientId, Double quantity);

        void deleteByWarehouseId(Long warehouseId);

        @Query("SELECT b FROM InventoryBatch b WHERE b.branch.id = :branchId ORDER BY b.importedAt DESC")
        List<InventoryBatch> findByBranchIdOrderByImportedAtDesc(@Param("branchId") Long branchId);

}