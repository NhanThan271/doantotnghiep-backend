package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.StaffStatus;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Override
    public Staff create(Long userId, Long branchId, StaffStatus status) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // 1 user chỉ thuộc 1 staff (tuỳ hệ thống)
        staffRepository.findByUserId(userId).ifPresent(s -> {
            throw new RuntimeException("User already assigned to a staff");
        });

        Staff staff = Staff.builder()
                .user(user)
                .branch(branch)
                .status(status)
                .build();

        return staffRepository.save(staff);
    }

    @Override
    public Staff updateStatus(Long id, StaffStatus status) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        staff.setStatus(status);

        return staffRepository.save(staff);
    }

    @Override
    public void delete(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new RuntimeException("Staff not found");
        }
        staffRepository.deleteById(id);
    }

    @Override
    public Staff getById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
    }

    @Override
    public List<Staff> getByBranch(Long branchId) {
        return staffRepository.findByBranchId(branchId);
    }

    @Override
    public List<Staff> getByStatus(StaffStatus status) {
        return staffRepository.findByStatus(status);
    }

    @Override
    public List<Staff> getByBranchAndStatus(Long branchId, StaffStatus status) {
        return staffRepository.findByBranchIdAndStatus(branchId, status);
    }
}