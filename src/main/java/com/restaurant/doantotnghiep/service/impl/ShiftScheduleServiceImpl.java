package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.entity.ShiftSchedule;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.ShiftRepository;
import com.restaurant.doantotnghiep.repository.ShiftScheduleRepository;
import com.restaurant.doantotnghiep.service.ShiftScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftScheduleServiceImpl implements ShiftScheduleService {

    private final ShiftScheduleRepository repository;
    private final BranchRepository branchRepository;
    private final ShiftRepository shiftRepository;

    @Override
    public ShiftSchedule create(ShiftSchedule request) {

        Branch branch = branchRepository.findById(
                request.getBranch().getId()).orElseThrow(() -> new RuntimeException("Branch not found"));

        Shift shift = shiftRepository.findById(
                request.getShift().getId()).orElseThrow(() -> new RuntimeException("Shift not found"));

        ShiftSchedule schedule = new ShiftSchedule();
        schedule.setWorkDay(request.getWorkDay());
        schedule.setBranch(branch);
        schedule.setShift(shift);
        schedule.setRequiredStaff(request.getRequiredStaff());
        schedule.setMaxStaff(request.getMaxStaff());

        return repository.save(schedule);
    }

    @Override
    public ShiftSchedule update(Long id, ShiftSchedule request) {

        ShiftSchedule schedule = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift schedule not found"));

        schedule.setWorkDay(request.getWorkDay());
        schedule.setRequiredStaff(request.getRequiredStaff());
        schedule.setMaxStaff(request.getMaxStaff());

        if (request.getBranch() != null) {
            Branch branch = branchRepository.findById(
                    request.getBranch().getId()).orElseThrow(() -> new RuntimeException("Branch not found"));

            schedule.setBranch(branch);
        }

        if (request.getShift() != null) {
            Shift shift = shiftRepository.findById(
                    request.getShift().getId()).orElseThrow(() -> new RuntimeException("Shift not found"));

            schedule.setShift(shift);
        }

        return repository.save(schedule);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public ShiftSchedule getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift schedule not found"));
    }

    @Override
    public List<ShiftSchedule> getAll() {
        return repository.findAll();
    }

    @Override
    public List<ShiftSchedule> getByWorkDay(LocalDate workDay) {
        return repository.findByWorkDay(workDay);
    }

    @Override
    public List<ShiftSchedule> getByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    @Override
    public List<ShiftSchedule> getByBranchAndWorkDay(Long branchId, LocalDate workDay) {
        return repository.findByBranchIdAndWorkDay(branchId, workDay);
    }
}