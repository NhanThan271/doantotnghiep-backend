package com.restaurant.doantotnghiep.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class FoodDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive;
}
