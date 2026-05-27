package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Warehouse;
import com.restaurant.doantotnghiep.entity.WarehouseInventory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

}
