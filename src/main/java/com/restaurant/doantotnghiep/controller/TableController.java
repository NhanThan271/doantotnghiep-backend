package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.TableEntity;
import com.restaurant.doantotnghiep.repository.TableRepository;
import com.restaurant.doantotnghiep.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/tables")
@CrossOrigin(origins = "http://localhost:3000")
public class TableController {

    @Autowired
    private TableService tableService;
    @Autowired
    private TableRepository tableRepository;

    @GetMapping
    public List<TableEntity> getAllTables() {
        return tableService.getAllTables();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER' , 'EMPLOYEE', 'CUSTOMER')")
    public TableEntity getTableById(@PathVariable Long id) {
        return tableService.getTableById(id);
    }

    @PutMapping("/{id}/occupy")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE', 'CUSTOMER')")
    public TableEntity occupyTable(@PathVariable Long id) {
        return tableService.occupyTable(id);
    }

    @PutMapping("/{id}/free")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER', 'EMPLOYEE')")
    public TableEntity freeTable(@PathVariable Long id) {
        return tableService.freeTable(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public TableEntity createTable(@RequestBody TableEntity table) {
        return tableService.createTable(table);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TableEntity updateTable(@PathVariable Long id, @RequestBody TableEntity table) {
        return tableService.updateTable(id, table);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
    }

    @GetMapping("/branch/{branchId}/areas")
    public List<String> getAreasByBranch(@PathVariable Long branchId) {
        return tableRepository.findDistinctAreasByBranchId(branchId);
    }

    @GetMapping("/branch/{branchId}/area/{area}")
    public List<TableEntity> getTablesByBranchAndArea(
            @PathVariable Long branchId,
            @PathVariable String area) {
        return tableRepository.findByBranchIdAndArea(branchId, area);
    }
}
