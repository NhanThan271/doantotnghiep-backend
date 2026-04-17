package com.restaurant.doantotnghiep.dto;

import lombok.Data;
import java.util.List;

@Data
public class InventoryRequestCreateDTO {
    private Long branchId;
    private Long warehouseId;
    private String type;
    private String reason;
    private List<ItemDTO> items;

    @Data
    public static class ItemDTO {
        private Long ingredientId;
        private Double quantity;
    }
}