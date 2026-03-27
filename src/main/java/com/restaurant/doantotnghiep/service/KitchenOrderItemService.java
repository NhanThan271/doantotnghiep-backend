package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

import java.util.List;

public interface KitchenOrderItemService {

    KitchenOrderItem create(Long kitchenOrderId, Long foodId);

    KitchenOrderItem updateStatus(Long id, KitchenStatus status);

    List<KitchenOrderItem> getByKitchenOrder(Long kitchenOrderId);

    List<KitchenOrderItem> getByStatus(KitchenStatus status);

    KitchenOrderItem getById(Long id);
}