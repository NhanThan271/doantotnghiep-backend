package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.WarehouseImportRequest;
import com.restaurant.doantotnghiep.entity.Warehouse;
import java.util.List;

public interface WarehouseService {
    void importWarehouse(WarehouseImportRequest request);

    Warehouse create(Warehouse warehouse);

    List<Warehouse> getAll();
}