package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.service.ShiftService;
import lombok.RequiredArgsConstructor;
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
    public Shift create(@RequestBody Shift shift) {
        return shiftService.create(shift);
    }

    // Update
    @PutMapping("/{id}")
    public Shift update(@PathVariable Long id, @RequestBody Shift shift) {
        return shiftService.update(id, shift);
    }

    // Xóa
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shiftService.delete(id);
    }

    // Lấy tất cả
    @GetMapping
    public List<Shift> getAll() {
        return shiftService.getAll();
    }

    // Lấy theo ID
    @GetMapping("/{id}")
    public Shift getById(@PathVariable Long id) {
        return shiftService.getById(id);
    }

    // Search theo tên
    @GetMapping("/search")
    public List<Shift> search(@RequestParam String name) {
        return shiftService.searchByName(name);
    }

    // Lọc theo thời gian
    @GetMapping("/time-range")
    public List<Shift> getByTimeRange(
            @RequestParam LocalTime start,
            @RequestParam LocalTime end) {
        return shiftService.getByTimeRange(start, end);
    }
}