package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.StaffDTO;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.StaffPosition;

import java.util.List;

public interface StaffService {

    Staff create(Long userId, Long branchId, StaffPosition position);

    StaffDTO updatePosition(Long id, StaffPosition position);

    void delete(Long id);

    Staff getById(Long id);

    List<StaffDTO> getByBranch(Long branchId);

    List<Staff> getByStatus(StaffPosition position);

    List<Staff> getByBranchAndStatus(Long branchId, StaffPosition position);
}