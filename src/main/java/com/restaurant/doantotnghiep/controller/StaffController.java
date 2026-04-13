package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.StaffDTO;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.StaffPosition;
import com.restaurant.doantotnghiep.service.StaffService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StaffController {

    private final StaffService service;

    // Tạo staff
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Staff create(
            @RequestParam Long userId,
            @RequestParam Long branchId,
            @RequestParam StaffPosition position) {
        return service.create(userId, branchId, position);
    }

    // Update vai trò (WAITER → CHEF…)
    @PutMapping("/{id}/position")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public StaffDTO updatePosition(
            @PathVariable Long id,
            @RequestParam StaffPosition position) {
        return service.updatePosition(id, position);
    }

    // Xóa staff
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // Lấy theo chi nhánh
    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<StaffDTO> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    // Lọc theo role staff
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Staff> getByStatus(@RequestParam StaffPosition position) {
        return service.getByStatus(position);
    }

    // Lọc theo branch + role
    @GetMapping("/branch/{branchId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Staff> getByBranchAndStatus(
            @PathVariable Long branchId,
            @RequestParam StaffPosition position) {
        return service.getByBranchAndStatus(branchId, position);
    }

    // Chi tiết
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Staff getById(@PathVariable Long id) {
        return service.getById(id);
    }
}