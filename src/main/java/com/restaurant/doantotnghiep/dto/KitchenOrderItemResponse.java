package com.restaurant.doantotnghiep.dto;

import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KitchenOrderItemResponse {
    private Long id;
    private String foodName;
    private KitchenStatus kitchenStatus;
}