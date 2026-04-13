package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.StaffDTO;
import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.StaffPosition;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Override
    public Staff create(Long userId, Long branchId, StaffPosition position) {

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
                .position(position)
                .build();

        return staffRepository.save(staff);
    }

    @Override
    public StaffDTO updatePosition(Long id, StaffPosition position) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        staff.setPosition(position);

        Staff saved = staffRepository.save(staff);

        return new StaffDTO(
                saved.getId(),
                saved.getUser().getId(),
                saved.getUser().getUsername(),
                saved.getUser().getFullName(),
                saved.getPosition() != null ? saved.getPosition().name() : null,
                saved.getStatus() != null ? saved.getStatus().name() : null,
                saved.getBranch().getId());
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
    public List<StaffDTO> getByBranch(Long branchId) {
        return staffRepository.findByBranchId(branchId)
                .stream()
                .map(s -> new StaffDTO(
                        s.getId(),
                        s.getUser().getId(),
                        s.getUser().getUsername(),
                        s.getUser().getFullName(),
                        s.getPosition() != null ? s.getPosition().name() : null,
                        s.getStatus() != null ? s.getStatus().name() : null,
                        s.getBranch().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Staff> getByStatus(StaffPosition status) {
        return staffRepository.findByStatus(status);
    }

    @Override
    public List<Staff> getByBranchAndStatus(Long branchId, StaffPosition status) {
        return staffRepository.findByBranchIdAndStatus(branchId, status);
    }
}