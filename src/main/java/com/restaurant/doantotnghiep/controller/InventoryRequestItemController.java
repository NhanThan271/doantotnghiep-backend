package com.restaurant.doantotnghiep.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.restaurant.doantotnghiep.entity.InventoryRequestItem;
import com.restaurant.doantotnghiep.service.InventoryRequestItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-request-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryRequestItemController {

    private final InventoryRequestItemService service;

    // thêm nguyên liệu vào phiếu yêu cầu
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public InventoryRequestItem addItem(
            @RequestParam Long requestId,
            @RequestParam Long ingredientId,
            @RequestParam Double quantity) {

        return service.addItem(requestId, ingredientId, quantity);
    }

    // sửa số lượng
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public InventoryRequestItem updateQuantity(
            @PathVariable Long id,
            @RequestParam Double quantity) {

        return service.updateQuantity(id, quantity);
    }

    // xoá item
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public void deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
    }

    // xem chi tiết yêu cầu
    @GetMapping("/request/{requestId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<InventoryRequestItem> getByRequest(
            @PathVariable Long requestId) {

        return service.getItemsByRequest(requestId);
    }
}
