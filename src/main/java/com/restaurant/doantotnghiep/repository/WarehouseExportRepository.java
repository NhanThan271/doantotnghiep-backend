package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.WarehouseExport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarehouseExportRepository extends JpaRepository<WarehouseExport, Long> {
        List<WarehouseExport> findAllByOrderByCreatedAtDesc();
}