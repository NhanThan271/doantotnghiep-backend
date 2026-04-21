package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.service.ShiftService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ShiftController {

    private final ShiftService shiftService;

    // Tạo ca
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Shift create(@RequestBody Shift shift) {
        return shiftService.create(shift);
    }

    // Update
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Shift update(@PathVariable Long id, @RequestBody Shift shift) {
        return shiftService.update(id, shift);
    }

    // Xóa
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void delete(@PathVariable Long id) {
        shiftService.delete(id);
    }

    // Lấy tất cả
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<Shift> getAll() {
        return shiftService.getAll();
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public Shift getById(@PathVariable Long id) {
        return shiftService.getById(id);
    }

    // Search theo tên
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<Shift> search(@RequestParam String name) {
        return shiftService.searchByName(name);
    }

    // Lọc theo thời gian
    @GetMapping("/time-range")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<Shift> getByTimeRange(
            @RequestParam LocalTime start,
            @RequestParam LocalTime end) {
        return shiftService.getByTimeRange(start, end);
    }
}