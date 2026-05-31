package com.restaurant.doantotnghiep.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CloseSessionRequest {

    private BigDecimal actualCash;

    private String note;
}
