package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.PromotionDTO;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.Promotion;
import com.restaurant.doantotnghiep.repository.PromotionRepository;
import com.restaurant.doantotnghiep.service.PromotionService;
import com.restaurant.doantotnghiep.repository.BranchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "http://localhost:3000")
public class PromotionController {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private BranchRepository branchRepository;

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/test-branch/{branchId}")
    public ResponseEntity<?> testBranch(@PathVariable Long branchId) {
        try {
            Optional<Branch> branch = branchRepository.findById(branchId);
            if (branch.isPresent()) {
                Branch b = branch.get();
                Map<String, Object> result = new HashMap<>();
                result.put("found", true);
                result.put("id", b.getId());
                result.put("name", b.getName());
                result.put("address", b.getAddress());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.ok(Map.of("found", false, "message", "Branch not found"));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping
    public List<PromotionDTO> getAllPromotions() {
        LocalDate today = LocalDate.now();
        return promotionService.getAllPromotions().stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive())
                        && (p.getStartDate() == null || !p.getStartDate().isAfter(today))
                        && (p.getEndDate() == null || !p.getEndDate().isBefore(today)))
                .toList();
    }

    @GetMapping("/all") // nếu muốn lấy hết (kể cả hết hạn)
    public List<Promotion> getAllIncludingExpired() {
        return promotionRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPromotion(@RequestBody PromotionDTO promotionDTO) {
        try {
            System.out.println("📝 Received DTO: " + promotionDTO);
            System.out.println("🏢 Branch IDs: " + promotionDTO.getBranchIds());

            PromotionDTO result = promotionService.createPromotion(promotionDTO);

            System.out.println("Created successfully: " + result);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Error creating promotion: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", e.getMessage(),
                            "type", e.getClass().getSimpleName()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PromotionDTO> updatePromotion(
            @PathVariable Long id,
            @RequestBody PromotionDTO promotionDTO) {
        try {
            return ResponseEntity.ok(promotionService.updatePromotion(id, promotionDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all-dto")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<PromotionDTO> getAllPromotionsDTO() {
        return promotionService.getAllPromotions();
    }
}
