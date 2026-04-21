package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.StaffShiftDTO;
import com.restaurant.doantotnghiep.entity.StaffShift;

import java.time.LocalDate;
import java.util.List;

public interface StaffShiftService {

    StaffShiftDTO assignShift(Long staffId, Long shiftId, LocalDate workDay);

    StaffShiftDTO update(Long id, Long shiftId, LocalDate workDay);

    void delete(Long id);

    StaffShift getById(Long id);

    List<StaffShift> getAll();

    List<StaffShift> getByStaff(Long staffId);

    List<StaffShift> getByStaffAndDate(Long staffId, LocalDate date);

    List<StaffShiftDTO> getByDate(LocalDate date);
}