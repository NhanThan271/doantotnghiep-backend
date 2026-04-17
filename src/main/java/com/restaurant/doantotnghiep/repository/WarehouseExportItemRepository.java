package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.WarehouseExportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WarehouseExportItemRepository extends JpaRepository<WarehouseExportItem, Long> {
        List<WarehouseExportItem> findByExportId(Long exportId);
}