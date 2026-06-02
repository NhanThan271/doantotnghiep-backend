package com.restaurant.doantotnghiep.controller;

import java.util.List;

import com.restaurant.doantotnghiep.dto.KitchenOrderItemResponse;
import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import com.restaurant.doantotnghiep.service.KitchenService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/kitchen")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;

    @GetMapping("/queue")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public List<OrderItem> getKitchenQueue() {
        return kitchenService.getKitchenQueue();
    }

    @PutMapping("/order-items/{id}/status")
    public KitchenOrderItemResponse updateStatus(
            @PathVariable Long id,
            @RequestParam KitchenStatus status,
            @RequestParam(defaultValue = "1") int quantity) {
        return kitchenService.updateKitchenStatus(id, status, quantity);
    }

    @PutMapping("/order-items/{id}/status-only")
    public KitchenOrderItemResponse updateStatusOnly(
            @PathVariable Long id,
            @RequestParam KitchenStatus status) {
        return kitchenService.updateKitchenStatusOnly(id, status);
    }
}
