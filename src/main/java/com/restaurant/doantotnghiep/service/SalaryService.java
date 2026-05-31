package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.SalaryResponse;
import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.AttendanceStatus;
import com.restaurant.doantotnghiep.repository.AttendanceRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final StaffRepository staffRepo;
    private final AttendanceRepository attendanceRepo;

    public SalaryResponse calculateSalary(Long staffId, int month, int year) {

        Staff staff = staffRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        EmploymentType type = staff.getEmploymentType();
        if (type == null) {
            SalaryResponse res = new SalaryResponse();
            res.setStaffId(staffId);
            res.setMonth(month);
            res.setYear(year);
            res.setEmploymentType("Chưa phân loại");
            res.setTotalDaysWorked(0L);
            res.setLateDays(0L);
            res.setBaseSalary(staff.getBaseSalary() != null ? staff.getBaseSalary() : 0.0);
            res.setTotalSalary(0.0);
            return res;
        }

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> list = attendanceRepo.findByStaffAndMonth(
                staff,
                start,
                end);

        long lateDays = list.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LATE)
                .count();

        long workDays = list.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT
                        || a.getStatus() == AttendanceStatus.LATE)
                .count();

        double salary = 0;

        // FULLTIME
        if (type.getName().equalsIgnoreCase("FULLTIME")) {

            double base = staff.getBaseSalary();

            salary = base * type.getSalaryCoefficient();

            // phạt đi trễ (ví dụ)
            salary -= lateDays * 50000;

            // thưởng chuyên cần
            if (workDays >= type.getMaxShiftPerMonth()) {
                salary += 300000;
            }
        }

        // PARTTIME
        else if (type.getName().equalsIgnoreCase("PARTTIME")) {

            double hourlyRate = staff.getHourlyRate();

            double totalHours = list.stream()
                    .filter(a -> a.getStatus() == AttendanceStatus.PRESENT
                            || a.getStatus() == AttendanceStatus.LATE)
                    .mapToDouble(a -> a.getShiftSchedule()
                            .getShift()
                            .getWorkingHours())
                    .sum();

            salary = totalHours * hourlyRate;
        }

        SalaryResponse res = new SalaryResponse();
        res.setStaffId(staffId);
        res.setMonth(month);
        res.setYear(year);
        res.setEmploymentType(type.getName());
        res.setTotalDaysWorked(workDays);
        res.setLateDays(lateDays);
        res.setBaseSalary(staff.getBaseSalary());
        res.setTotalSalary(salary);

        return res;
    }
}