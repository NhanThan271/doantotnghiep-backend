package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.StaffStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByBranchId(Long branchId);

    List<Staff> findByStatus(StaffStatus status);

    List<Staff> findByBranchIdAndStatus(Long branchId, StaffStatus status);

    Optional<Staff> findByUserId(Long userId);
}