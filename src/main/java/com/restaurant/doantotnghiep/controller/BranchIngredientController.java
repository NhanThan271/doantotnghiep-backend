package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.BranchIngredient;
import com.restaurant.doantotnghiep.service.BranchIngredientService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branch-ingredients")
@CrossOrigin(origins = "http://localhost:3000")
public class BranchIngredientController {

    private final BranchIngredientService branchIngredientService;

    public BranchIngredientController(BranchIngredientService branchIngredientService) {
        this.branchIngredientService = branchIngredientService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<BranchIngredient> getAll() {
        return branchIngredientService.getAll();
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public List<BranchIngredient> getByBranch(@PathVariable Long branchId) {
        return branchIngredientService.getByBranch(branchId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public BranchIngredient getById(@PathVariable Long id) {
        return branchIngredientService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BranchIngredient create(
            @RequestParam Long branchId,
            @RequestParam Long ingredientId,
            @RequestParam Double quantity) {
        return branchIngredientService.create(branchId, ingredientId, quantity);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public BranchIngredient updateQuantity(
            @PathVariable Long id,
            @RequestParam Double quantity) {
        return branchIngredientService.updateQuantity(id, quantity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        branchIngredientService.delete(id);
    }
}
