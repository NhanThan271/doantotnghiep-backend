package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryBatchRepository extends JpaRepository<InventoryBatch, Long> {

    // Lấy batch theo chi nhánh + nguyên liệu
    List<InventoryBatch> findByBranchIdAndIngredientIdOrderByImportedAtAsc(
            Long branchId, Long ingredientId);

    // Lấy batch còn hàng
    List<InventoryBatch> findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanOrderByImportedAtAsc(
            Long branchId, Long ingredientId, Double quantity);

}