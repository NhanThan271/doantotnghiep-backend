package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.InventoryRequest;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.service.InventoryRequestService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-requests")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryRequestController {

    private final InventoryRequestService service;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public InventoryRequest create(
            @RequestBody InventoryRequest request,
            @AuthenticationPrincipal User currentUser) {
        return service.create(request, currentUser);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryRequest approve(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return service.approve(id, currentUser);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryRequest reject(
            @PathVariable Long id,
            @RequestBody String note,
            @AuthenticationPrincipal User currentUser) {
        return service.reject(id, note, currentUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<InventoryRequest> getAll() {
        return service.getAll();
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasRole('MANAGER')")
    public List<InventoryRequest> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventoryRequest getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
