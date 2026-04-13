package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.StaffPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByBranchId(Long branchId);

    List<Staff> findByStatus(StaffPosition status);

    List<Staff> findByBranchIdAndStatus(Long branchId, StaffPosition status);

    Optional<Staff> findByUserId(Long userId);
}