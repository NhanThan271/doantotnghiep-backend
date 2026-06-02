package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.InventoryBatchDTO;
import com.restaurant.doantotnghiep.entity.InventoryBatch;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InventoryBatchService {

    InventoryBatch create(Long branchId, Long ingredientId,
            Double quantity, LocalDate expiryDate);

    InventoryBatch getById(Long id);

    List<InventoryBatch> getAll();

    List<InventoryBatch> getByBranchAndIngredient(Long branchId, Long ingredientId);

    void delete(Long id);

    List<InventoryBatchDTO> getByBranchAsDTO(Long branchId);

    List<InventoryBatch> getNearExpired();

    List<InventoryBatch> getWarehouseBatches(Long warehouseId);

    List<Map<String, Object>> getAggregatedByWarehouse(Long warehouseId);

    List<Map<String, Object>> getAggregatedByBranch(Long branchId);
}