package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.KitchenOrderItemService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KitchenOrderItemServiceImpl implements KitchenOrderItemService {

    private final KitchenOrderItemRepository repository;
    private final KitchenOrderRepository kitchenOrderRepository;
    private final FoodRepository foodRepository;

    @Override
    public KitchenOrderItem create(Long kitchenOrderId, Long foodId) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(kitchenOrderId)
                .orElseThrow(() -> new RuntimeException("KitchenOrder not found"));

        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));

        KitchenOrderItem item = KitchenOrderItem.builder()
                .kitchenOrder(kitchenOrder)
                .food(food)
                .kitchenStatus(KitchenStatus.PREPARING)
                .build();

        return repository.save(item);
    }

    @Override
    @Transactional
    public KitchenOrderItem updateStatus(Long id, KitchenStatus status) {
        KitchenOrderItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("KitchenOrderItem not found"));

        item.setKitchenStatus(status);
        repository.save(item);

        if (status == KitchenStatus.READY) {
            Long kitchenOrderId = item.getKitchenOrder().getId();
            boolean allReady = repository.findByKitchenOrderId(kitchenOrderId)
                    .stream()
                    .allMatch(i -> i.getKitchenStatus() == KitchenStatus.READY);

            if (allReady) {
                KitchenOrder ko = item.getKitchenOrder();
                ko.setKitchenStatus(KitchenOrderStatus.DONE);
                kitchenOrderRepository.save(ko);
            }
        }

        return item;
    }

    @Override
    public List<KitchenOrderItem> getByKitchenOrder(Long kitchenOrderId) {
        return repository.findByKitchenOrderId(kitchenOrderId);
    }

    @Override
    public List<KitchenOrderItem> getByStatus(KitchenStatus status) {
        return repository.findByKitchenStatus(status);
    }

    @Override
    public KitchenOrderItem getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("KitchenOrderItem not found"));
    }
}