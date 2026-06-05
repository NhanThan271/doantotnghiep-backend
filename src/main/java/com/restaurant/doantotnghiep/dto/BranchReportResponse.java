package com.restaurant.doantotnghiep.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BranchReportResponse {

    private Long branchId;
    private String branchName;
    private BigDecimal totalRevenue;
    private BigDecimal cashRevenue;
    private BigDecimal transferRevenue;
    private Integer totalOrders;
}