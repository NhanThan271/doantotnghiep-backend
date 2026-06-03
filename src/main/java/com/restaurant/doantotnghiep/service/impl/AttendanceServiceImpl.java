package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.MonthlyAttendanceResponse;
import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.AttendanceStatus;
import com.restaurant.doantotnghiep.repository.AttendanceRepository;
import com.restaurant.doantotnghiep.repository.ShiftScheduleRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import com.restaurant.doantotnghiep.service.AttendanceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

        private final AttendanceRepository attendanceRepository;
        private final StaffRepository staffRepository;
        private final ShiftScheduleRepository shiftScheduleRepository;

        @Override
        @Transactional
        public Attendance checkIn(Long staffId, Long shiftScheduleId) {

                Staff staff = staffRepository.findById(staffId)
                                .orElseThrow(() -> new RuntimeException("Staff not found"));

                ShiftSchedule schedule = shiftScheduleRepository.findById(shiftScheduleId)
                                .orElseThrow(() -> new RuntimeException("Shift schedule not found"));

                Attendance attendance = attendanceRepository
                                .findByStaffAndShiftSchedule(staff, schedule)
                                .orElse(null);

                if (attendance == null) {
                        attendance = new Attendance();
                        attendance.setStaff(staff);
                        attendance.setShiftSchedule(schedule);
                }

                if (attendance.getCheckIn() != null) {
                        throw new RuntimeException("Already checked in");
                }

                attendance.setCheckIn(LocalDateTime.now());

                if (LocalDateTime.now().isAfter(
                                schedule.getShift().getStartTime()
                                                .atDate(schedule.getWorkDay()))) {

                        attendance.setStatus(AttendanceStatus.LATE);

                } else {

                        attendance.setStatus(AttendanceStatus.PRESENT);
                }

                return attendanceRepository.save(attendance);
        }

        @Override
        @Transactional
        public Attendance checkOut(Long staffId, Long shiftScheduleId) {

                Staff staff = staffRepository.findById(staffId)
                                .orElseThrow(() -> new RuntimeException("Staff not found"));

                ShiftSchedule schedule = shiftScheduleRepository.findById(shiftScheduleId)
                                .orElseThrow(() -> new RuntimeException("Shift schedule not found"));

                Attendance attendance = attendanceRepository
                                .findByStaffAndShiftSchedule(staff, schedule)
                                .orElseThrow(() -> new RuntimeException("Not checked in"));

                if (attendance.getCheckOut() != null) {
                        throw new RuntimeException("Already checked out");
                }

                attendance.setCheckOut(LocalDateTime.now());

                return attendanceRepository.save(attendance);
        }

        @Override
        public Attendance getAttendance(
                        Long staffId,
                        Long shiftScheduleId) {

                Staff staff = staffRepository.findById(staffId)
                                .orElseThrow(() -> new RuntimeException("Staff not found"));

                ShiftSchedule schedule = shiftScheduleRepository.findById(shiftScheduleId)
                                .orElseThrow(() -> new RuntimeException("Shift schedule not found"));

                return attendanceRepository
                                .findByStaffAndShiftSchedule(staff, schedule)
                                .orElse(null);
        }

        @Override
        public MonthlyAttendanceResponse getMonthlyReport(
                        Long staffId,
                        int month,
                        int year) {

                Staff staff = staffRepository.findById(staffId)
                                .orElseThrow(() -> new RuntimeException("Staff not found"));

                LocalDate start = LocalDate.of(year, month, 1);
                LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

                List<Attendance> monthlyAttendance = attendanceRepository.findByStaffAndMonth(
                                staff,
                                start,
                                end);

                long present = monthlyAttendance.stream()
                                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                                .count();

                long late = monthlyAttendance.stream()
                                .filter(a -> a.getStatus() == AttendanceStatus.LATE)
                                .count();

                long leave = monthlyAttendance.stream()
                                .filter(a -> a.getStatus() == AttendanceStatus.LEAVE)
                                .count();

                MonthlyAttendanceResponse response = new MonthlyAttendanceResponse();

                response.setStaffId(staffId);
                response.setMonth(month);
                response.setYear(year);

                response.setTotalDays(monthlyAttendance.size());
                response.setPresentDays(present);
                response.setLateDays(late);
                response.setLeaveDays(leave);

                return response;
        }
}