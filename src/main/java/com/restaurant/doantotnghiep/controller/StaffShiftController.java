package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.StaffShiftDTO;
import com.restaurant.doantotnghiep.dto.WeeklyScheduleDTO;
import com.restaurant.doantotnghiep.entity.StaffShift;
import com.restaurant.doantotnghiep.service.StaffShiftService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff-shifts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class StaffShiftController {

    private final StaffShiftService staffShiftService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public StaffShiftDTO assign(
            @RequestParam Long staffId,
            @RequestParam Long shiftId,
            @RequestParam LocalDate workDay) {
        return staffShiftService.assignShift(staffId, shiftId, workDay);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public StaffShiftDTO update(
            @PathVariable Long id,
            @RequestParam Long shiftId,
            @RequestParam LocalDate workDay) {
        return staffShiftService.update(id, shiftId, workDay);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public void delete(@PathVariable Long id) {
        staffShiftService.delete(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<StaffShift> getAll() {
        return staffShiftService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public StaffShift getById(@PathVariable Long id) {
        return staffShiftService.getById(id);
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<StaffShiftDTO> getByStaff(@PathVariable Long staffId) {
        return staffShiftService.getByStaff(staffId)
                .stream()
                .map(staffShiftService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/date")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<StaffShiftDTO> getByDate(@RequestParam LocalDate date) {
        return staffShiftService.getByDate(date);
    }

    @GetMapping("/staff-date")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<StaffShiftDTO> getByStaffAndDate(
            @RequestParam Long staffId,
            @RequestParam LocalDate date) {
        return staffShiftService.getByStaffAndDate(staffId, date)
                .stream()
                .map(staffShiftService::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/staff/{staffId}/week")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public List<StaffShiftDTO> getWeeklySchedule(
            @PathVariable Long staffId,
            @RequestParam LocalDate startDate) {

        return staffShiftService
                .getWeeklySchedule(staffId, startDate);
    }

    @GetMapping("/manager/week")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<WeeklyScheduleDTO> getManagerWeeklySchedule(
            @RequestParam Long branchId,
            @RequestParam LocalDate startDate) {

        return staffShiftService
                .findByBranchIdAndWorkDayRange(branchId, startDate);
    }

}