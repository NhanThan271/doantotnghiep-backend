package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import com.restaurant.doantotnghiep.service.KitchenOrderItemService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen-order-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class KitchenOrderItemController {

    private final KitchenOrderItemService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public KitchenOrderItem create(
            @RequestParam Long kitchenOrderId,
            @RequestParam Long foodId) {
        return service.create(kitchenOrderId, foodId);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public KitchenOrderItem updateStatus(
            @PathVariable Long id,
            @RequestParam KitchenStatus status) {
        return service.updateStatus(id, status);
    }

    @GetMapping("/kitchen-order/{kitchenOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<KitchenOrderItem> getByKitchenOrder(@PathVariable Long kitchenOrderId) {
        return service.getByKitchenOrder(kitchenOrderId);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<KitchenOrderItem> getByStatus(
            @RequestParam KitchenStatus status) {
        return service.getByStatus(status);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public KitchenOrderItem getById(@PathVariable Long id) {
        return service.getById(id);
    }
}