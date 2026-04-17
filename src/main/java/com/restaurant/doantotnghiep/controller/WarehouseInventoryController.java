package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.WarehouseInventory;
import com.restaurant.doantotnghiep.repository.WarehouseInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse-inventory")
@RequiredArgsConstructor
public class WarehouseInventoryController {

    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<WarehouseInventory> getInventory(
            @RequestParam(required = false) Long warehouseId) {

        if (warehouseId != null) {
            return warehouseInventoryRepository.findByWarehouseId(warehouseId);
        }
        return warehouseInventoryRepository.findAll();
    }
}