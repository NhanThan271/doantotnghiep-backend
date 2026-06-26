package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.*;
import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientPreparationService {

    private final ReservationItemRepository reservationItemRepository;
    private final RecipeRepository recipeRepository;
    private final BranchIngredientRepository branchIngredientRepository;
    private final InventoryBatchRepository inventoryBatchRepository;

    public IngredientPreparationSummaryDTO calculate(
            Long branchId, LocalDateTime from, LocalDateTime to) {

        // 1. Lấy tất cả ReservationItem trong khoảng
        List<ReservationItem> items = reservationItemRepository.findItemsForPreparation(branchId, from, to);

        // Đếm số reservation duy nhất
        long totalReservations = items.stream()
                .map(i -> i.getReservation().getId())
                .distinct().count();

        // 2. Gom: foodId -> tổng quantity món đặt
        Map<Long, Integer> foodQuantityMap = new HashMap<>();
        for (ReservationItem item : items) {
            Long foodId = item.getBranchFood().getFood().getId();
            foodQuantityMap.merge(foodId, item.getQuantity(), Integer::sum);
        }

        // 3. Lấy Recipe cho các food này
        List<Recipe> recipes = recipeRepository.findByFoodIds(
                new ArrayList<>(foodQuantityMap.keySet()));

        // 4. Tính tổng nguyên liệu cần: ingredientId -> totalRequired
        Map<Long, Double> requiredMap = new HashMap<>();
        for (Recipe recipe : recipes) {
            Long ingId = recipe.getIngredient().getId();
            Integer foodQty = foodQuantityMap.get(recipe.getFood().getId());
            double needed = recipe.getQuantityRequired() * foodQty;
            requiredMap.merge(ingId, needed, Double::sum);
        }

        // 5. Lấy tồn kho và lô
        Map<Long, Double> stockMap = branchIngredientRepository
                .findByBranchId(branchId).stream()
                .collect(Collectors.toMap(
                        bi -> bi.getIngredient().getId(),
                        BranchIngredient::getQuantity));

        Map<Long, List<InventoryBatch>> batchMap = inventoryBatchRepository
                .findAvailableBatchesByBranch(branchId).stream()
                .collect(Collectors.groupingBy(ib -> ib.getIngredient().getId()));

        // 6. Map ingredient info từ recipes (tránh query thêm)
        Map<Long, Ingredient> ingredientInfoMap = recipes.stream()
                .collect(Collectors.toMap(
                        r -> r.getIngredient().getId(),
                        Recipe::getIngredient,
                        (a, b) -> a));

        // 7. Build kết quả
        List<IngredientPreparationDTO> result = requiredMap.entrySet().stream()
                .map(entry -> {
                    Long ingId = entry.getKey();
                    double required = entry.getValue();
                    double stock = stockMap.getOrDefault(ingId, 0.0);
                    double shortage = Math.max(0, required - stock);

                    Ingredient ing = ingredientInfoMap.get(ingId);

                    List<IngredientPreparationDTO.BatchInfo> batches = batchMap.getOrDefault(ingId, List.of()).stream()
                            .map(b -> IngredientPreparationDTO.BatchInfo.builder()
                                    .batchId(b.getId())
                                    .remainingQuantity(b.getRemainingQuantity())
                                    .expiryDate(b.getExpiryDate())
                                    .importedAt(b.getImportedAt())
                                    .build())
                            .collect(Collectors.toList());

                    return IngredientPreparationDTO.builder()
                            .ingredientId(ingId)
                            .ingredientName(ing != null ? ing.getName() : "Unknown")
                            .unit(ing != null ? ing.getUnit() : "")
                            .totalRequired(required)
                            .currentStock(stock)
                            .shortage(shortage)
                            .batches(batches)
                            .build();
                })
                // Ưu tiên hiển thị nguyên liệu thiếu lên đầu
                .sorted(Comparator.comparingDouble(IngredientPreparationDTO::getShortage).reversed())
                .collect(Collectors.toList());

        return IngredientPreparationSummaryDTO.builder()
                .branchId(branchId)
                .fromDate(from)
                .toDate(to)
                .totalReservations((int) totalReservations)
                .ingredients(result)
                .build();
    }
}
