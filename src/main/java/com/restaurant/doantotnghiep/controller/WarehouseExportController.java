package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.WarehouseExport;
import com.restaurant.doantotnghiep.entity.WarehouseExportItem;
import com.restaurant.doantotnghiep.repository.WarehouseExportItemRepository;
import com.restaurant.doantotnghiep.repository.WarehouseExportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse-exports")
@RequiredArgsConstructor
public class WarehouseExportController {

    private final WarehouseExportRepository exportRepository;
    private final WarehouseExportItemRepository exportItemRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<WarehouseExport> getAll() {
        return exportRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> getById(@PathVariable Long id) {
        WarehouseExport export = exportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        List<WarehouseExportItem> items = exportItemRepository.findByExportId(id);
        return Map.of("export", export, "items", items);
    }
}
