package com.restaurant.doantotnghiep.dto;

import java.util.List;

import lombok.Data;

@Data
public class RecipeRequest {

    private Long foodId;

    private List<IngredientItem> ingredients;

    @Data
    public static class IngredientItem {
        private Long ingredientId;
        private Double quantityRequired;
    }
}
