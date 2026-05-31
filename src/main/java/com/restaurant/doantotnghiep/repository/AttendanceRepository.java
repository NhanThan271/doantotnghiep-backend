package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Attendance;
import com.restaurant.doantotnghiep.entity.ShiftSchedule;
import com.restaurant.doantotnghiep.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByStaffAndShiftSchedule(
            Staff staff,
            ShiftSchedule shiftSchedule);

    List<Attendance> findByStaff(Staff staff);

    List<Attendance> findByShiftSchedule(ShiftSchedule shiftSchedule);

    @Query("""
            SELECT a
            FROM Attendance a
            WHERE a.staff = :staff
              AND a.shiftSchedule.workDay BETWEEN :start AND :end
            """)
    List<Attendance> findByStaffAndMonth(
            @Param("staff") Staff staff,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

}