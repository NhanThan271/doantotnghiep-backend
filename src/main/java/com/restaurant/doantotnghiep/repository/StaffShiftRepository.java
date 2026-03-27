package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffShiftRepository extends JpaRepository<StaffShift, Long> {

    List<StaffShift> findByStaffId(Long staffId);

    List<StaffShift> findByWorkDay(LocalDate workDay);

    List<StaffShift> findByStaffIdAndWorkDay(Long staffId, LocalDate workDay);

    Optional<StaffShift> findByStaffIdAndShiftIdAndWorkDay(
            Long staffId, Long shiftId, LocalDate workDay);
}