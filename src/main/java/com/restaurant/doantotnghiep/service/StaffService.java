package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.StaffStatus;

import java.util.List;

public interface StaffService {

    Staff create(Long userId, Long branchId, StaffStatus status);

    Staff updateStatus(Long id, StaffStatus status);

    void delete(Long id);

    Staff getById(Long id);

    List<Staff> getByBranch(Long branchId);

    List<Staff> getByStatus(StaffStatus status);

    List<Staff> getByBranchAndStatus(Long branchId, StaffStatus status);
}