package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.repository.ShiftRepository;
import com.restaurant.doantotnghiep.service.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;

    @Override
    public Shift create(Shift shift) {
        validateShift(shift);
        return shiftRepository.save(shift);
    }

    @Override
    public Shift update(Long id, Shift shift) {
        Shift existing = getById(id);

        existing.setName(shift.getName());
        existing.setStartTime(shift.getStartTime());
        existing.setEndTime(shift.getEndTime());

        validateShift(existing);

        return shiftRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        shiftRepository.deleteById(id);
    }

    @Override
    public Shift getById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
    }

    @Override
    public List<Shift> getAll() {
        return shiftRepository.findAll();
    }

    @Override
    public List<Shift> searchByName(String name) {
        return shiftRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Shift> getByTimeRange(LocalTime start, LocalTime end) {
        return shiftRepository
                .findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(start, end);
    }

    // validate business
    private void validateShift(Shift shift) {
        if (shift.getStartTime().isAfter(shift.getEndTime())) {
            throw new RuntimeException("Start time must be before end time");
        }
    }
}