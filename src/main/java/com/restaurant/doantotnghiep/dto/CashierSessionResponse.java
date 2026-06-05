package com.restaurant.doantotnghiep.dto;

import com.restaurant.doantotnghiep.entity.enums.CashierSessionStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CashierSessionResponse {

    private Long id;
    private Long staffId;
    private String staffName;
    private Long branchId;
    private String branchName;

    private BigDecimal openingCash;
    private BigDecimal cashRevenue;
    private BigDecimal transferRevenue;
    private BigDecimal totalRevenue;

    private Integer totalOrders;
    private BigDecimal expectedCash;
    private BigDecimal actualCash;
    private BigDecimal differenceAmount;
    private String note;

    private LocalDateTime openedAt;
    private LocalDateTime closedAt;

    private CashierSessionStatus status;
}