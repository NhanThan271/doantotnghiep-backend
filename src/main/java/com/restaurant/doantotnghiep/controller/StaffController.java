package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.StaffStatus;
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
            @RequestParam StaffStatus status) {
        return service.create(userId, branchId, status);
    }

    // Update vai trò (WAITER → CHEF…)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Staff updateStatus(
            @PathVariable Long id,
            @RequestParam StaffStatus status) {
        return service.updateStatus(id, status);
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
    public List<Staff> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    // Lọc theo role staff
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Staff> getByStatus(@RequestParam StaffStatus status) {
        return service.getByStatus(status);
    }

    // Lọc theo branch + role
    @GetMapping("/branch/{branchId}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<Staff> getByBranchAndStatus(
            @PathVariable Long branchId,
            @RequestParam StaffStatus status) {
        return service.getByBranchAndStatus(branchId, status);
    }

    // Chi tiết
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Staff getById(@PathVariable Long id) {
        return service.getById(id);
    }
}