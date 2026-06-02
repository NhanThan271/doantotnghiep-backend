package com.restaurant.doantotnghiep.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import com.restaurant.doantotnghiep.dto.KitchenOrderItemResponse;
import com.restaurant.doantotnghiep.entity.InventoryBatch;
import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.Recipe;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.repository.InventoryBatchRepository;
import com.restaurant.doantotnghiep.repository.KitchenOrderItemRepository;
import com.restaurant.doantotnghiep.repository.KitchenOrderRepository;
import com.restaurant.doantotnghiep.repository.OrderItemRepository;
import com.restaurant.doantotnghiep.repository.RecipeRepository;
import com.restaurant.doantotnghiep.service.KitchenService;
import com.restaurant.doantotnghiep.service.OrderService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private final OrderItemRepository orderItemRepository;
    private final KitchenOrderItemRepository kitchenOrderItemRepository;
    private final KitchenOrderRepository kitchenOrderRepository;
    private final RecipeRepository recipeRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    @Lazy
    private final OrderService orderService;

    @Override
    public List<OrderItem> getKitchenQueue() {
        return orderItemRepository
                .findByKitchenStatusNotOrderByCreatedAtAsc(KitchenStatus.READY);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public KitchenOrderItemResponse updateKitchenStatus(Long kitchenOrderItemId, KitchenStatus status, int quantity) {

        KitchenOrderItem item = kitchenOrderItemRepository
                .findByIdForUpdate(kitchenOrderItemId)
                .orElseThrow(() -> new RuntimeException("Kitchen order item not found: " + kitchenOrderItemId));

        KitchenStatus currentStatus = item.getKitchenStatus();
        if (status == KitchenStatus.PREPARING && currentStatus == KitchenStatus.PREPARING) {
            System.out.println("Already PREPARING, skip deduction for id=" + kitchenOrderItemId);
            return KitchenOrderItemResponse.builder()
                    .id(item.getId())
                    .foodName(item.getFood().getName())
                    .kitchenStatus(item.getKitchenStatus())
                    .build();
        }

        boolean shouldDeduct = status == KitchenStatus.PREPARING && currentStatus != KitchenStatus.PREPARING;

        if (shouldDeduct) {
            Long branchId = item.getKitchenOrder().getOrder().getBranch().getId();
            Long foodId = item.getFood().getId();
            List<Recipe> recipes = recipeRepository.findByFoodId(foodId);

            for (Recipe recipe : recipes) {
                Long ingredientId = recipe.getIngredient().getId();
                double required = recipe.getQuantityRequired() * quantity;

                double remain = required;
                List<InventoryBatch> batches = inventoryBatchRepository
                        .findByBranchIdAndIngredientIdAndRemainingQuantityGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
                                branchId, ingredientId, 0.0, LocalDate.now());

                for (InventoryBatch batch : batches) {
                    if (remain <= 0)
                        break;
                    double used = Math.min(batch.getRemainingQuantity(), remain);
                    batch.setRemainingQuantity(batch.getRemainingQuantity() - used);
                    inventoryBatchRepository.save(batch);
                    remain -= used;
                }

                if (remain > 0)
                    throw new RuntimeException("Không đủ nguyên liệu: " + recipe.getIngredient().getName());
            }
        }

        item.setKitchenStatus(status);
        kitchenOrderItemRepository.save(item);

        if (status == KitchenStatus.READY) {
            KitchenOrder kitchenOrder = item.getKitchenOrder();
            Long kitchenOrderId = kitchenOrder.getId();

            boolean allReady = kitchenOrderItemRepository
                    .findByKitchenOrderId(kitchenOrderId)
                    .stream()
                    .allMatch(i -> i.getKitchenStatus() == KitchenStatus.READY);

            if (allReady) {
                kitchenOrder.setKitchenStatus(KitchenOrderStatus.DONE);
                kitchenOrderRepository.save(kitchenOrder);

                Long orderId = kitchenOrder.getOrder().getId();
                orderService.updateOrderStatus(orderId, OrderStatus.COMPLETED);
            }
        }

        return KitchenOrderItemResponse.builder()
                .id(item.getId())
                .foodName(item.getFood().getName())
                .kitchenStatus(item.getKitchenStatus())
                .build();
    }

    @Override
    @Transactional
    public KitchenOrderItemResponse updateKitchenStatusOnly(Long kitchenOrderItemId, KitchenStatus status) {
        KitchenOrderItem item = kitchenOrderItemRepository.findById(kitchenOrderItemId)
                .orElseThrow(() -> new RuntimeException("Kitchen order item not found: " + kitchenOrderItemId));

        System.out.println("=== updateKitchenStatusOnly ===");
        System.out.println("id=" + kitchenOrderItemId + " status=" + status);

        item.setKitchenStatus(status);
        kitchenOrderItemRepository.save(item);

        if (status == KitchenStatus.READY) {
            KitchenOrder kitchenOrder = item.getKitchenOrder();
            boolean allReady = kitchenOrderItemRepository
                    .findByKitchenOrderId(kitchenOrder.getId())
                    .stream()
                    .allMatch(i -> i.getKitchenStatus() == KitchenStatus.READY);

            if (allReady) {
                kitchenOrder.setKitchenStatus(KitchenOrderStatus.DONE);
                kitchenOrderRepository.save(kitchenOrder);
                orderService.updateOrderStatus(kitchenOrder.getOrder().getId(), OrderStatus.COMPLETED);
            }
        }

        return KitchenOrderItemResponse.builder()
                .id(item.getId())
                .foodName(item.getFood().getName())
                .kitchenStatus(item.getKitchenStatus())
                .build();
    }
}
