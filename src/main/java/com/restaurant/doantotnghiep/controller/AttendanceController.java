package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.MonthlyAttendanceResponse;
import com.restaurant.doantotnghiep.entity.Attendance;
import com.restaurant.doantotnghiep.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in/{staffId}")
    public Attendance checkIn(
            @PathVariable Long staffId,
            @RequestParam Long shiftScheduleId) {
        return attendanceService.checkIn(staffId, shiftScheduleId);
    }

    @PostMapping("/check-out/{staffId}")
    public Attendance checkOut(
            @PathVariable Long staffId,
            @RequestParam Long shiftScheduleId) {
        return attendanceService.checkOut(staffId, shiftScheduleId);
    }

    @GetMapping("/today/{staffId}")
    public Attendance getToday(
            @PathVariable Long staffId,
            @RequestParam Long shiftScheduleId) {
        return attendanceService.getAttendance(staffId, shiftScheduleId);
    }

    @GetMapping("/monthly/{staffId}")
    public MonthlyAttendanceResponse getMonthly(
            @PathVariable Long staffId,
            @RequestParam int month,
            @RequestParam int year) {
        return attendanceService.getMonthlyReport(staffId, month, year);
    }
}