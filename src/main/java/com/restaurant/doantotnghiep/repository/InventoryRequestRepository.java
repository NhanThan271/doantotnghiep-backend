package com.restaurant.doantotnghiep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.doantotnghiep.entity.InventoryRequest;
import com.restaurant.doantotnghiep.entity.enums.RequestStatus;

public interface InventoryRequestRepository extends JpaRepository<InventoryRequest, Long> {

    List<InventoryRequest> findByBranchId(Long branchId);

    List<InventoryRequest> findByStatus(RequestStatus status);
}
