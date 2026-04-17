package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.WarehouseImportRequest;
import com.restaurant.doantotnghiep.entity.Warehouse;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;
import com.restaurant.doantotnghiep.repository.WarehouseInventoryRepository;
import com.restaurant.doantotnghiep.service.WarehouseService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Warehouse create(@RequestBody Warehouse warehouse) {
        return warehouseService.create(warehouse);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Warehouse> getAll() {
        return warehouseService.getAll();
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public String importWarehouse(@RequestBody WarehouseImportRequest request) {
        warehouseService.importWarehouse(request);
        return "Nhập kho thành công";
    }

    @GetMapping("/inventory-history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<WarehouseInventory> getInventoryHistory() {
        return warehouseInventoryRepository.findAll();
    }
}
