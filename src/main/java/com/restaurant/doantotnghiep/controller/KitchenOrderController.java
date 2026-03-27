package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import com.restaurant.doantotnghiep.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kitchen-orders")
@CrossOrigin(origins = "http://localhost:3000")
public class KitchenOrderController {

    private final KitchenOrderService kitchenOrderService;

    // Tạo order cho bếp
    @PostMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
    public KitchenOrder create(@PathVariable Long orderId) {
        return kitchenOrderService.create(orderId);
    }

    // Update trạng thái
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public KitchenOrder updateStatus(
            @PathVariable Long id,
            @RequestParam KitchenOrderStatus status) {
        return kitchenOrderService.updateStatus(id, status);
    }

    // Lấy theo order
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE', 'MANAGER')")
    public List<KitchenOrder> getByOrder(@PathVariable Long orderId) {
        return kitchenOrderService.getByOrder(orderId);
    }

    // Lấy theo trạng thái
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE', 'MANAGER')")
    public List<KitchenOrder> getByStatus(
            @RequestParam KitchenOrderStatus status) {
        return kitchenOrderService.getByStatus(status);
    }

    // Danh sách đang làm
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<KitchenOrder> getActive() {
        return kitchenOrderService.getActiveKitchenOrders();
    }

    // Lấy chi tiết
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public KitchenOrder getById(@PathVariable Long id) {
        return kitchenOrderService.getById(id);
    }
}