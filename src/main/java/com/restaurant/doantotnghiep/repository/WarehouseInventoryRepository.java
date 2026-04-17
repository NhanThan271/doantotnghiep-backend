package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface WarehouseInventoryRepository
        extends JpaRepository<WarehouseInventory, Long> {

    Optional<WarehouseInventory> findByWarehouseIdAndIngredientId(Long warehouseId, Long ingredientId);

    List<WarehouseInventory> findByWarehouseId(Long warehouseId);
}
