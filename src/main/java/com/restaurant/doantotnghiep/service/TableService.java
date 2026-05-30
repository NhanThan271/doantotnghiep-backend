package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.TableEntity;
import com.restaurant.doantotnghiep.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface TableService {
    List<TableEntity> getAllTables();

    TableEntity getTableById(Long id);

    TableEntity createTable(TableEntity table);

    TableEntity updateTable(Long id, TableEntity table);

    void deleteTable(Long id);

    TableEntity updateTableStatus(Long id, Status status);

    List<TableEntity> getAvailableTables(
            Long branchId,
            LocalDateTime checkIn,
            LocalDateTime checkOut);
}
