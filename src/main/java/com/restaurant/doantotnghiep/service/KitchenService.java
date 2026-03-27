package com.restaurant.doantotnghiep.service;

import java.util.List;

import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

public interface KitchenService {

    List<OrderItem> getKitchenQueue();

    OrderItem updateKitchenStatus(Long orderItemId, KitchenStatus status);
}
