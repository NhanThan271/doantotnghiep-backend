package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.WarehouseExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WarehouseExportRepository extends JpaRepository<WarehouseExport, Long> {
        List<WarehouseExport> findAllByOrderByCreatedAtDesc();

        @Query("SELECT e FROM WarehouseExport e LEFT JOIN FETCH e.createdBy LEFT JOIN FETCH e.warehouse LEFT JOIN FETCH e.branch")
        List<WarehouseExport> findAllWithDetails();
}