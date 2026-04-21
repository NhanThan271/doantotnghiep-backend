package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.StaffShiftDTO;
import com.restaurant.doantotnghiep.entity.Shift;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.StaffShift;
import com.restaurant.doantotnghiep.repository.ShiftRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;
import com.restaurant.doantotnghiep.repository.StaffShiftRepository;
import com.restaurant.doantotnghiep.service.StaffShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffShiftServiceImpl implements StaffShiftService {

    private final StaffShiftRepository staffShiftRepository;
    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;

    @Override
    public StaffShiftDTO assignShift(Long staffId, Long shiftId, LocalDate workDay) {

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

        StaffShift saved = staffShiftRepository.save(staffShift);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public StaffShiftDTO update(Long id, Long shiftId, LocalDate workDay) {
        StaffShift existing = getById(id);

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new RuntimeException("Shift not found"));

        existing.setShift(shift);
        existing.setWorkDay(workDay);

        StaffShift saved = staffShiftRepository.save(existing);
        return toDTO(saved);
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
    public List<StaffShift> getByStaffAndDate(Long staffId, LocalDate date) {
        return staffShiftRepository.findByStaffIdAndWorkDay(staffId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StaffShiftDTO> getByDate(LocalDate date) {
        return staffShiftRepository.findByWorkDay(date)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private StaffShiftDTO toDTO(StaffShift ss) {
        StaffShiftDTO.ShiftInfo shiftInfo = null;
        if (ss.getShift() != null) {
            shiftInfo = StaffShiftDTO.ShiftInfo.builder()
                    .id(ss.getShift().getId())
                    .name(ss.getShift().getName())
                    .startTime(ss.getShift().getStartTime().toString())
                    .endTime(ss.getShift().getEndTime().toString())
                    .build();
        }

        StaffShiftDTO.StaffInfo staffInfo = null;
        if (ss.getStaff() != null) {
            StaffShiftDTO.UserInfo userInfo = null;
            if (ss.getStaff().getUser() != null) {
                userInfo = StaffShiftDTO.UserInfo.builder()
                        .id(ss.getStaff().getUser().getId())
                        .fullName(ss.getStaff().getUser().getFullName())
                        .username(ss.getStaff().getUser().getUsername())
                        .imageUrl(ss.getStaff().getUser().getImageUrl())
                        .build();
            }
            staffInfo = StaffShiftDTO.StaffInfo.builder()
                    .id(ss.getStaff().getId())
                    .position(ss.getStaff().getPosition() != null
                            ? ss.getStaff().getPosition().name()
                            : null)
                    .user(userInfo)
                    .build();
        }

        return StaffShiftDTO.builder()
                .id(ss.getId())
                .workDay(ss.getWorkDay())
                .shift(shiftInfo)
                .staff(staffInfo)
                .build();
    }
}