package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.StaffShift;
import com.restaurant.doantotnghiep.repository.ShiftRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import com.restaurant.doantotnghiep.repository.StaffShiftRepository;
import com.restaurant.doantotnghiep.service.StaffShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffShiftServiceImpl implements StaffShiftService {

    private final StaffShiftRepository staffShiftRepository;
    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;

    @Override
    public StaffShift assignShift(Long staffId, Long shiftId, LocalDate workDay) {

        // check trùng ca
        staffShiftRepository
                .findByStaffIdAndShiftIdAndWorkDay(staffId, shiftId, workDay)
                .ifPresent(s -> {
                    throw new RuntimeException("Staff already assigned to this shift");
                });

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        StaffShift staffShift = StaffShift.builder()
                .staff(staff)
                .shift(shift)
                .workDay(workDay)
                .build();

        return staffShiftRepository.save(staffShift);
    }

    @Override
    public StaffShift update(Long id, Long shiftId, LocalDate workDay) {
        StaffShift existing = getById(id);

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        existing.setShift(shift);
        existing.setWorkDay(workDay);

        return staffShiftRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        staffShiftRepository.deleteById(id);
    }

    @Override
    public StaffShift getById(Long id) {
        return staffShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StaffShift not found"));
    }

    @Override
    public List<StaffShift> getAll() {
        return staffShiftRepository.findAll();
    }

    @Override
    public List<StaffShift> getByStaff(Long staffId) {
        return staffShiftRepository.findByStaffId(staffId);
    }

    @Override
    public List<StaffShift> getByDate(LocalDate date) {
        return staffShiftRepository.findByWorkDay(date);
    }

    @Override
    public List<StaffShift> getByStaffAndDate(Long staffId, LocalDate date) {
        return staffShiftRepository.findByStaffIdAndWorkDay(staffId, date);
    }
}