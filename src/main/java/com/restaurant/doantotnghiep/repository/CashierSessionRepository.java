package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.CashierSession;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.enums.CashierSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CashierSessionRepository
        extends JpaRepository<CashierSession, Long> {

    Optional<CashierSession> findByStaffAndStatus(
            Staff staff,
            CashierSessionStatus status);

    List<CashierSession> findByStaffId(Long staffId);

    List<CashierSession> findByBranchId(Long branchId);

    List<CashierSession> findByOpenedAtBetween(
            LocalDateTime start,
            LocalDateTime end);

    boolean existsByStaffAndStatus(
            Staff staff,
            CashierSessionStatus status);

    List<CashierSession> findAllByOrderByOpenedAtDesc();

}