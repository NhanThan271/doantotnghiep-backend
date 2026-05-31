package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.EmploymentType;
import com.restaurant.doantotnghiep.service.EmploymentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employment-types")
@RequiredArgsConstructor
public class EmploymentTypeController {

    private final EmploymentTypeService service;

    @GetMapping
    public List<EmploymentType> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EmploymentType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public EmploymentType create(@RequestBody EmploymentType employmentType) {
        return service.create(employmentType);
    }

    @PutMapping("/{id}")
    public EmploymentType update(
            @PathVariable Long id,
            @RequestBody EmploymentType employmentType) {
        return service.update(id, employmentType);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}