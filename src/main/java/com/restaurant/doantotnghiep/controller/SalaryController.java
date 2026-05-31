package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.SalaryResponse;
import com.restaurant.doantotnghiep.service.SalaryService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @GetMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public SalaryResponse getSalary(
            @PathVariable Long staffId,
            @RequestParam int month,
            @RequestParam int year
    ) {
        return salaryService.calculateSalary(staffId, month, year);
    }
}