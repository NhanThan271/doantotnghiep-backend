package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.MonthlyAttendanceResponse;
import com.restaurant.doantotnghiep.entity.Attendance;
import com.restaurant.doantotnghiep.service.AttendanceService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public Attendance checkIn(
            @PathVariable Long staffId,
            @RequestParam Long shiftScheduleId) {
        return attendanceService.checkIn(staffId, shiftScheduleId);
    }

    @PostMapping("/check-out/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public Attendance checkOut(
            @PathVariable Long staffId,
            @RequestParam Long shiftScheduleId) {
        return attendanceService.checkOut(staffId, shiftScheduleId);
    }

    @GetMapping("/today/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public Attendance getToday(
            @PathVariable Long staffId,
            @RequestParam Long shiftScheduleId) {
        return attendanceService.getAttendance(staffId, shiftScheduleId);
    }

    @GetMapping("/monthly/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public MonthlyAttendanceResponse getMonthly(
            @PathVariable Long staffId,
            @RequestParam int month,
            @RequestParam int year) {
        return attendanceService.getMonthlyReport(staffId, month, year);
    }
}