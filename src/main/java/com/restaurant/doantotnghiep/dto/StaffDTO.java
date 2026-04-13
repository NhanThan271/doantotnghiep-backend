package com.restaurant.doantotnghiep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffDTO {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String position;
    private String status;
    private Long branchId;
}
