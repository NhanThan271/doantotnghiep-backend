package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.TableEntity;
import java.util.List;

public interface TableService {
    List<TableEntity> getAllTables();

    TableEntity getTableById(Long id);

    TableEntity createTable(TableEntity table);

    TableEntity updateTable(Long id, TableEntity table);

    void deleteTable(Long id);

    TableEntity occupyTable(Long id); // chọn bàn

    TableEntity freeTable(Long id); // trả bàn

    TableEntity reserveTable(Long id); // đặt bàn trước
}
