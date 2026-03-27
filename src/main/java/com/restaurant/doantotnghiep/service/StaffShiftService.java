package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.StaffShift;

import java.time.LocalDate;
import java.util.List;

public interface StaffShiftService {

    StaffShift assignShift(Long staffId, Long shiftId, LocalDate workDay);

    StaffShift update(Long id, Long shiftId, LocalDate workDay);

    void delete(Long id);

    StaffShift getById(Long id);

    List<StaffShift> getAll();

    List<StaffShift> getByStaff(Long staffId);

    List<StaffShift> getByDate(LocalDate date);

    List<StaffShift> getByStaffAndDate(Long staffId, LocalDate date);
}