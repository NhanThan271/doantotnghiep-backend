package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.InventoryBatchDTO;
import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.repository.InventoryBatchRepository;
import com.restaurant.doantotnghiep.service.InventoryBatchService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory-batches")
@CrossOrigin(origins = "*")
public class InventoryBatchController {

    private final InventoryBatchService batchService;
    private final InventoryBatchRepository batchRepository;

    public InventoryBatchController(InventoryBatchService batchService, InventoryBatchRepository batchRepository) {
        this.batchService = batchService;
        this.batchRepository = batchRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public InventoryBatch create(
            @RequestParam Long branchId,
            @RequestParam Long ingredientId,
            @RequestParam Double quantity,
            @RequestParam(required = false) String expiryDate) {

        LocalDate date = expiryDate != null ? LocalDate.parse(expiryDate) : null;

        return batchService.create(branchId, ingredientId, quantity, date);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<InventoryBatch> getAll() {
        return batchService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public InventoryBatch getById(@PathVariable Long id) {
        return batchService.getById(id);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<InventoryBatch> getByBranchAndIngredient(
            @RequestParam Long branchId,
            @RequestParam Long ingredientId) {

        return batchService.getByBranchAndIngredient(branchId, ingredientId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        batchService.delete(id);
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<InventoryBatchDTO> getByBranch(@PathVariable Long branchId) {
        return batchService.getByBranchAsDTO(branchId);
    }

    @GetMapping("/near-expiry")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<InventoryBatch> getNearExpiry() {
        return batchService.getNearExpired();
    }

    @GetMapping("/warehouse/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<InventoryBatch> getWarehouseBatches(
            @PathVariable Long warehouseId) {

        return batchService
                .getWarehouseBatches(warehouseId);
    }

    @GetMapping("/expired-count")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Long countExpired() {
        return batchRepository.countByExpiryDateBefore(
                LocalDate.now());
    }

    @GetMapping("/warehouse/{warehouseId}/aggregated")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAggregatedByWarehouse(
            @PathVariable Long warehouseId) {
        return ResponseEntity.ok(batchService.getAggregatedByWarehouse(warehouseId));
    }

    @GetMapping("/branch/{branchId}/aggregated")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<Map<String, Object>>> getAggregatedByBranch(
            @PathVariable Long branchId) {
        return ResponseEntity.ok(batchService.getAggregatedByBranch(branchId));
    }
}