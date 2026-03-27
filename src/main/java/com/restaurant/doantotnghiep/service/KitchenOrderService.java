package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;

import java.util.List;

public interface KitchenOrderService {

    KitchenOrder create(Long orderId);

    KitchenOrder updateStatus(Long id, KitchenOrderStatus status);

    List<KitchenOrder> getByOrder(Long orderId);

    List<KitchenOrder> getByStatus(KitchenOrderStatus status);

    List<KitchenOrder> getActiveKitchenOrders();

    KitchenOrder getById(Long id);
}