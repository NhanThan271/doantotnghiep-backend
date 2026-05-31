package com.restaurant.doantotnghiep.dto;

import lombok.Data;

@Data
public class SalaryResponse {

    private Long staffId;

    private int month;
    private int year;

    private String employmentType;

    private long totalDaysWorked;

    private long lateDays;

    private double baseSalary;

    private double totalSalary;
}