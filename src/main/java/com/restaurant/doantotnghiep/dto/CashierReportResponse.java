package com.restaurant.doantotnghiep.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CashierReportResponse {

    private BigDecimal totalRevenue;
    private BigDecimal cashRevenue;
    private BigDecimal transferRevenue;
    private Integer totalOrders;
}