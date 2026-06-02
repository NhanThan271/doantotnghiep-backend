package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByWorkDay(LocalDate workDay);

    List<ShiftSchedule> findByBranchId(Long branchId);

    List<ShiftSchedule> findByBranchIdAndWorkDay(Long branchId, LocalDate workDay);

    List<ShiftSchedule> findByWorkDayBetween(
            LocalDate startDate,
            LocalDate endDate);

    @Query("SELECT s FROM ShiftSchedule s WHERE s.branch.id = :branchId AND s.workDay BETWEEN :startDate AND :endDate")
    List<ShiftSchedule> findByBranchIdAndWorkDayRange(
            @Param("branchId") Long branchId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

}