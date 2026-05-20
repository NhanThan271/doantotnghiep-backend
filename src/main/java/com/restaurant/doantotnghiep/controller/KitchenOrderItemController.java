package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import com.restaurant.doantotnghiep.service.KitchenOrderItemService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PreAuthorize("hasAnyRole('EMPLOYEE','ADMIN','MANAGER')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam KitchenStatus status) {
        try {
            KitchenOrderItem item = service.updateStatus(id, status);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("id", item.getId());
            result.put("kitchenStatus", item.getKitchenStatus());
            if (item.getFood() != null) {
                result.put("foodId", item.getFood().getId());
                result.put("foodName", item.getFood().getName());
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
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

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public ResponseEntity<?> getActiveItems() {
        try {
            List<KitchenOrderItem> items = service.getActiveItems();

            List<Map<String, Object>> result = items.stream().map(item -> {
                Map<String, Object> dto = new java.util.HashMap<>();
                dto.put("id", item.getId());
                dto.put("kitchenStatus", item.getKitchenStatus());

                // Food
                if (item.getFood() != null) {
                    Map<String, Object> food = new java.util.HashMap<>();
                    food.put("id", item.getFood().getId());
                    food.put("name", item.getFood().getName());
                    dto.put("food", food);
                }

                // KitchenOrder → Order → Table + Branch
                if (item.getKitchenOrder() != null && item.getKitchenOrder().getOrder() != null) {
                    var order = item.getKitchenOrder().getOrder();
                    dto.put("orderId", order.getId());
                    dto.put("createdAt", order.getCreatedAt());

                    if (order.getTable() != null) {
                        Map<String, Object> table = new java.util.HashMap<>();
                        table.put("id", order.getTable().getId());
                        table.put("number", order.getTable().getNumber());
                        dto.put("table", table);
                    }

                    if (order.getBranch() != null) {
                        Map<String, Object> branch = new java.util.HashMap<>();
                        branch.put("id", order.getBranch().getId());
                        dto.put("branch", branch);
                    }

                    // Lấy quantity từ order_items
                    if (order.getItems() != null && item.getFood() != null) {
                        order.getItems().stream()
                                .filter(oi -> oi.getFood() != null &&
                                        oi.getFood().getId().equals(item.getFood().getId()))
                                .findFirst()
                                .ifPresent(oi -> dto.put("quantity", oi.getQuantity()));
                    }
                }

                if (!dto.containsKey("quantity")) {
                    dto.put("quantity", 1);
                }

                return dto;
            }).collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}