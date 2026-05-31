package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.ShiftSchedule;

import java.time.LocalDate;
import java.util.List;

public interface ShiftScheduleService {

    ShiftSchedule create(ShiftSchedule shiftSchedule);

    ShiftSchedule update(Long id, ShiftSchedule shiftSchedule);

    void delete(Long id);

    ShiftSchedule getById(Long id);

    List<ShiftSchedule> getAll();

    List<ShiftSchedule> getByWorkDay(LocalDate workDay);

    List<ShiftSchedule> getByBranch(Long branchId);

    List<ShiftSchedule> getByBranchAndWorkDay(Long branchId, LocalDate workDay);
}