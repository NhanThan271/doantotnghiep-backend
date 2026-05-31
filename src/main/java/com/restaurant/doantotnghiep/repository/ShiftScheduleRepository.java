package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByWorkDay(LocalDate workDay);

    List<ShiftSchedule> findByBranchId(Long branchId);

    List<ShiftSchedule> findByBranchIdAndWorkDay(Long branchId, LocalDate workDay);

}