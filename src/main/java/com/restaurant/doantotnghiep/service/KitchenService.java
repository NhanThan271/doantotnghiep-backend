package com.restaurant.doantotnghiep.service;

import java.util.List;

import com.restaurant.doantotnghiep.dto.KitchenOrderItemResponse;
import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

public interface KitchenService {

    List<OrderItem> getKitchenQueue();

    KitchenOrderItemResponse updateKitchenStatus(Long id, KitchenStatus status, int quantity);

    KitchenOrderItemResponse updateKitchenStatusOnly(Long id, KitchenStatus status);
}
