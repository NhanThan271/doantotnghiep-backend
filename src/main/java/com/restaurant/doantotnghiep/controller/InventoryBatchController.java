package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.service.InventoryBatchService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-batches")
@CrossOrigin(origins = "*")
public class InventoryBatchController {

    private final InventoryBatchService batchService;

    public InventoryBatchController(InventoryBatchService batchService) {
        this.batchService = batchService;
    }

    // tạo batch (nhập kho)
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

    // lấy tất cả
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<InventoryBatch> getAll() {
        return batchService.getAll();
    }

    // lấy theo id
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public InventoryBatch getById(@PathVariable Long id) {
        return batchService.getById(id);
    }

    // lấy theo branch + ingredient
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<InventoryBatch> getByBranchAndIngredient(
            @RequestParam Long branchId,
            @RequestParam Long ingredientId) {

        return batchService.getByBranchAndIngredient(branchId, ingredientId);
    }

    // xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        batchService.delete(id);
    }
}