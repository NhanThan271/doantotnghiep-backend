package com.restaurant.doantotnghiep.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.restaurant.doantotnghiep.dto.FoodWithPromotionDTO;
import com.restaurant.doantotnghiep.entity.BranchFood;
import com.restaurant.doantotnghiep.service.BranchFoodService;

@RestController
@RequestMapping("/api/branch-foods")
@CrossOrigin(origins = "http://localhost:3000")
public class BranchFoodController {

    @Autowired
    private BranchFoodService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public BranchFood save(@RequestBody BranchFood bf) {
        return service.save(bf);
    }

    @GetMapping("/branch/{branchId}")
    public List<BranchFood> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    @GetMapping("/branch/{branchId}/with-promotions")
    public ResponseEntity<List<FoodWithPromotionDTO>> getFoodsWithPromotions(
            @PathVariable Long branchId) {
        List<FoodWithPromotionDTO> foods = service.getFoodsWithPromotionByBranch(branchId);
        return ResponseEntity.ok(foods);
    }

    @PutMapping("/{branchFoodId}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<BranchFood> toggleFoodStatus(
            @PathVariable Long branchFoodId,
            @RequestBody java.util.Map<String, Boolean> payload) {

        BranchFood bf = service.findById(branchFoodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));

        bf.setIsActive(payload.get("isActive"));
        bf.setUpdatedAt(java.time.LocalDateTime.now());

        BranchFood updated = service.save(bf);
        return ResponseEntity.ok(updated);
    }

}
