package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.StaffShift;
import com.restaurant.doantotnghiep.service.StaffShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/staff-shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StaffShiftController {

    private final StaffShiftService staffShiftService;

    @PostMapping
    public StaffShift assign(
            @RequestParam Long staffId,
            @RequestParam Long shiftId,
            @RequestParam LocalDate workDay) {
        return staffShiftService.assignShift(staffId, shiftId, workDay);
    }

    @PutMapping("/{id}")
    public StaffShift update(
            @PathVariable Long id,
            @RequestParam Long shiftId,
            @RequestParam LocalDate workDay) {
        return staffShiftService.update(id, shiftId, workDay);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        staffShiftService.delete(id);
    }

    @GetMapping
    public List<StaffShift> getAll() {
        return staffShiftService.getAll();
    }

    @GetMapping("/{id}")
    public StaffShift getById(@PathVariable Long id) {
        return staffShiftService.getById(id);
    }

    @GetMapping("/staff/{staffId}")
    public List<StaffShift> getByStaff(@PathVariable Long staffId) {
        return staffShiftService.getByStaff(staffId);
    }

    @GetMapping("/date")
    public List<StaffShift> getByDate(@RequestParam LocalDate date) {
        return staffShiftService.getByDate(date);
    }

    @GetMapping("/staff-date")
    public List<StaffShift> getByStaffAndDate(
            @RequestParam Long staffId,
            @RequestParam LocalDate date) {
        return staffShiftService.getByStaffAndDate(staffId, date);
    }
}