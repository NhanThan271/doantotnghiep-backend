package com.restaurant.doantotnghiep.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OpenSessionRequest {

    private Long staffId;

    private Long branchId;

    private Long shiftId;

    private BigDecimal openingCash;
}
