package com.restaurant.doantotnghiep.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeeklyScheduleDTO {

    private LocalDate workDay;

    private Long shiftId;

    private String shiftName;

    private Integer requiredStaff;

    private Integer maxStaff;

    private Integer assignedStaff;

    private Integer missingStaff;
}