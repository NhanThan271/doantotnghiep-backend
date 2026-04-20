package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.InventoryRequestCreateDTO;
import com.restaurant.doantotnghiep.entity.InventoryRequest;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.service.InventoryRequestService;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory-requests")
@RequiredArgsConstructor
@CrossOrigin
public class InventoryRequestController {

    private final InventoryRequestService service;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public InventoryRequest create(
            @RequestBody InventoryRequestCreateDTO dto,
            Authentication authentication) {

        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return service.create(dto, currentUser);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryRequest approve(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return service.approve(id, currentUser);
    }

    @PutMapping("/{id}/confirm-received")
    @PreAuthorize("hasRole('MANAGER')")
    public InventoryRequest confirmReceived(
            @PathVariable Long id,
            Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return service.confirmReceived(id, currentUser);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryRequest reject(
            @PathVariable Long id,
            @RequestBody String note,
            Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
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
