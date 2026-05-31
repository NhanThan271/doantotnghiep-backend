package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.MonthlyAttendanceResponse;
import com.restaurant.doantotnghiep.entity.Attendance;

public interface AttendanceService {

    Attendance checkIn(Long staffId, Long shiftScheduleId);

    Attendance checkOut(Long staffId, Long shiftScheduleId);

    Attendance getAttendance(Long staffId, Long shiftScheduleId);

    MonthlyAttendanceResponse getMonthlyReport(
            Long staffId,
            int month,
            int year);

}