package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.MonthlyAttendanceResponse;
import com.restaurant.doantotnghiep.entity.Attendance;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.repository.AttendanceRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import com.restaurant.doantotnghiep.service.AttendanceService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;
    private final StaffRepository staffRepository;

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

    @GetMapping("/staff/{staffId}/month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public List<Attendance> getByStaffAndMonth(
            @PathVariable Long staffId,
            @RequestParam int month,
            @RequestParam int year) {

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findByStaffAndMonth(staff, start, end);
    }
}