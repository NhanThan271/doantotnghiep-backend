package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.ShiftSchedule;
import com.restaurant.doantotnghiep.service.ShiftScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shift-schedules")
@RequiredArgsConstructor
public class ShiftScheduleController {

    private final ShiftScheduleService service;

    @PostMapping
    public ShiftSchedule create(@RequestBody ShiftSchedule shiftSchedule) {
        return service.create(shiftSchedule);
    }

    @PutMapping("/{id}")
    public ShiftSchedule update(
            @PathVariable Long id,
            @RequestBody ShiftSchedule shiftSchedule) {

        return service.update(id, shiftSchedule);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}")
    public ShiftSchedule getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<ShiftSchedule> getAll() {
        return service.getAll();
    }

    @GetMapping("/work-day")
    public List<ShiftSchedule> getByWorkDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDay) {

        return service.getByWorkDay(workDay);
    }

    @GetMapping("/branch/{branchId}")
    public List<ShiftSchedule> getByBranch(
            @PathVariable Long branchId) {

        return service.getByBranch(branchId);
    }

    @GetMapping("/branch/{branchId}/work-day")
    public List<ShiftSchedule> getByBranchAndWorkDay(
            @PathVariable Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDay) {

        return service.getByBranchAndWorkDay(branchId, workDay);
    }
}