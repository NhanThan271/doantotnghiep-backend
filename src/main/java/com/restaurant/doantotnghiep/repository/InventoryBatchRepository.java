package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.dto.InventoryBatchDTO;
import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

        List<InventoryBatch> findByBranchIdAndIngredientIdOrderByImportedAtAsc(
                        Long branchId, Long ingredientId);

        List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanOrderByImportedAtAsc(
                        Long branchId, Long ingredientId, Double quantity);

        void deleteByWarehouseId(Long warehouseId);

        @Query("SELECT b FROM InventoryBatch b WHERE b.branch.id = :branchId ORDER BY b.importedAt DESC")
        List<InventoryBatch> findByBranchIdOrderByImportedAtDesc(@Param("branchId") Long branchId);

        List<InventoryBatch> findByWarehouseIdAndIngredientIdAndRemainingQuantityGreaterThanOrderByExpiryDateAsc(
                        Long warehouseId,
                        Long ingredientId,
                        Double quantity);

        List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanOrderByExpiryDateAsc(
                        Long branchId,
                        Long ingredientId,
                        Double quantity);

        @Query("""
                        SELECT b
                        FROM InventoryBatch b
                        WHERE b.branch.id = :branchId
                        AND b.ingredient.id = :ingredientId
                        AND b.remainingQuantity > 0
                        AND b.expiryDate >= CURRENT_DATE
                        ORDER BY b.expiryDate ASC
                        """)
        List<InventoryBatch> findAvailableBatchForCooking(
                        Long branchId,
                        Long ingredientId);

        List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
                        Long branchId,
                        Long ingredientId,
                        Double quantity,
                        LocalDate expiryDate);

        List<InventoryBatch> findByExpiryDateBetween(
                        LocalDate from,
                        LocalDate to);

        Long countByExpiryDateBefore(LocalDate date);

        List<InventoryBatch> findByWarehouseIdOrderByExpiryDateAsc(Long warehouseId);

        List<InventoryBatch> findByWarehouseIdAndIngredientIdAndRemainingQuantityGreaterThanAndExpiryDateGreaterThanOrderByExpiryDateAsc(
                        Long warehouseId,
                        Long ingredientId,
                        Double remainingQuantity,
                        LocalDate expiryDate);

        List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanAndExpiryDateGreaterThanOrderByExpiryDateAsc(
                        Long branchId,
                        Long ingredientId,
                        Double remainingQuantity,
                        LocalDate expiryDate);
}