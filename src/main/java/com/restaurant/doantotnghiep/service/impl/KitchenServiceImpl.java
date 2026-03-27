package com.restaurant.doantotnghiep.service.impl;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.repository.KitchenOrderRepository;
import com.restaurant.doantotnghiep.repository.OrderItemRepository;
import com.restaurant.doantotnghiep.service.KitchenService;
import com.restaurant.doantotnghiep.service.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private final OrderItemRepository orderItemRepository;
    private final KitchenOrderRepository kitchenOrderRepository;

    @Lazy
    private final OrderService orderService;

    @Override
    public List<OrderItem> getKitchenQueue() {
        return orderItemRepository
                .findByKitchenStatusNotOrderByCreatedAtAsc(KitchenStatus.READY);
    }

    @Override
    @Transactional
    public OrderItem updateKitchenStatus(Long orderItemId, KitchenStatus status) {

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        item.setKitchenStatus(status);
        orderItemRepository.save(item);

        boolean allReady = !orderItemRepository.existsByOrderIdAndKitchenStatusNot(
                item.getOrder().getId(), KitchenStatus.READY);

        if (allReady) {
            kitchenOrderRepository.findByOrderId(item.getOrder().getId())
                    .forEach(ko -> {
                        ko.setKitchenStatus(KitchenOrderStatus.DONE);
                        kitchenOrderRepository.save(ko);
                    });

            orderService.updateOrderStatus(item.getOrder().getId(), OrderStatus.COMPLETED);
        }

        return item;
    }
}
