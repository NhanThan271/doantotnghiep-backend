package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitchenOrderItemRepository extends JpaRepository<KitchenOrderItem, Long> {

    List<KitchenOrderItem> findByKitchenOrderId(Long kitchenOrderId);

    List<KitchenOrderItem> findByKitchenStatus(KitchenStatus status);

    List<KitchenOrderItem> findByKitchenOrderIdAndKitchenStatus(Long kitchenOrderId, KitchenStatus status);
}