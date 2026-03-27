package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen/order-items")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @Autowired
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public List<OrderItem> getAllItems() {
        return orderItemService.getAllItems();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public OrderItem getItemById(@PathVariable Long id) {
        return orderItemService.getItemById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public OrderItem createItem(@RequestBody OrderItem item) {
        return orderItemService.createItem(item);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public OrderItem updateItem(@PathVariable Long id, @RequestBody OrderItem item) {
        return orderItemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteItem(@PathVariable Long id) {
        orderItemService.deleteItem(id);
    }
}
