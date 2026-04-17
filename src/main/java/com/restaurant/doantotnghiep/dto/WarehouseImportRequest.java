package com.restaurant.doantotnghiep.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
public class WarehouseImportRequest {
    private Long warehouseId;
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private Long ingredientId;
        private Double quantity;
        private String expiryDate;
    }
}
