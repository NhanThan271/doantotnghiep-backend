package com.restaurant.doantotnghiep.dto;

import lombok.Data;

@Data
public class MonthlyAttendanceResponse {

    private Long staffId;

    private int month;
    private int year;

    private long totalDays;

    private long presentDays;

    private long lateDays;

    private long leaveDays;
}