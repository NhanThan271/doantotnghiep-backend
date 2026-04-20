package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.TableEntity;
import com.restaurant.doantotnghiep.entity.enums.Status;
import com.restaurant.doantotnghiep.repository.TableRepository;
import com.restaurant.doantotnghiep.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableRepository tableRepository;

    @Override
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public TableEntity getTableById(Long id) {
        return tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn có id = " + id));
    }

    @Override
    public TableEntity createTable(TableEntity table) {
        if (tableRepository.existsByBranchIdAndNumberAndArea(
                table.getBranch().getId(),
                table.getNumber(),
                table.getArea())) {
            throw new RuntimeException(
                    String.format("Bàn số %d ở %s đã tồn tại trong chi nhánh này!",
                            table.getNumber(),
                            table.getArea()));
        }

        if (table.getArea() == null || table.getArea().trim().isEmpty()) {
            throw new RuntimeException("Khu vực/Tầng không được để trống!");
        }
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        table.setStatus(Status.FREE); // bàn mới mặc định là trống
        return tableRepository.save(table);
    }

    @Override
    public TableEntity updateTable(Long id, TableEntity table) {
        TableEntity existing = getTableById(id);
        if (table.getArea() == null || table.getArea().trim().isEmpty()) {
            throw new RuntimeException("Khu vực/Tầng không được để trống!");
        }

        // Validate: Nếu đổi number hoặc area, kiểm tra trùng
        if (!existing.getNumber().equals(table.getNumber())
                || !existing.getArea().equals(table.getArea())) {

            if (tableRepository.existsByBranchIdAndNumberAndAreaAndIdNot(
                    table.getBranch().getId(),
                    table.getNumber(),
                    table.getArea(),
                    id)) {
                throw new RuntimeException(
                        String.format("Bàn số %d ở %s đã tồn tại trong chi nhánh này!",
                                table.getNumber(),
                                table.getArea()));
            }
        }
        existing.setNumber(table.getNumber());
        existing.setCapacity(table.getCapacity());
        existing.setArea(table.getArea());
        existing.setBranch(table.getBranch());
        existing.setStatus(table.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(existing);
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public TableEntity updateTableStatus(Long id, Status status) {
        TableEntity table = getTableById(id);
        table.setStatus(status);
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepository.save(table);
    }
}
