package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import com.restaurant.doantotnghiep.repository.KitchenOrderRepository;
import com.restaurant.doantotnghiep.repository.OrderRepository;
import com.restaurant.doantotnghiep.service.KitchenOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KitchenOrderServiceImpl implements KitchenOrderService {

    private final KitchenOrderRepository kitchenOrderRepository;
    private final OrderRepository orderRepository;

    @Override
    public KitchenOrder create(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        KitchenOrder kitchenOrder = KitchenOrder.builder()
                .order(order)
                .kitchenStatus(KitchenOrderStatus.WAITING)
                .build();

        return kitchenOrderRepository.save(kitchenOrder);
    }

    @Override
    public KitchenOrder updateStatus(Long id, KitchenOrderStatus status) {
        KitchenOrder ko = getById(id);

        if (ko.getKitchenStatus() == KitchenOrderStatus.DONE) {
            throw new RuntimeException("Order already completed");
        }

        ko.setKitchenStatus(status);

        return kitchenOrderRepository.save(ko);
    }

    @Override
    public List<KitchenOrder> getByOrder(Long orderId) {
        return kitchenOrderRepository.findByOrderId(orderId);
    }

    @Override
    public List<KitchenOrder> getByStatus(KitchenOrderStatus status) {
        return kitchenOrderRepository.findByKitchenStatus(status);
    }

    @Override
    public List<KitchenOrder> getActiveKitchenOrders() {
        return kitchenOrderRepository.findByKitchenStatusIn(
                List.of(KitchenOrderStatus.WAITING, KitchenOrderStatus.PREPARING));
    }

    @Override
    public KitchenOrder getById(Long id) {
        return kitchenOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("KitchenOrder not found"));
    }
}