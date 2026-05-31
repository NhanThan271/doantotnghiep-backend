package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KitchenOrderItemRepository extends JpaRepository<KitchenOrderItem, Long> {

    List<KitchenOrderItem> findByKitchenOrderId(Long kitchenOrderId);

    List<KitchenOrderItem> findByKitchenStatus(KitchenStatus status);

    List<KitchenOrderItem> findByKitchenOrderIdAndKitchenStatus(Long kitchenOrderId, KitchenStatus status);

    List<KitchenOrderItem> findByKitchenStatusIn(List<KitchenStatus> statuses);

    @Query("""
                SELECT DISTINCT koi
                FROM KitchenOrderItem koi
                LEFT JOIN FETCH koi.food
                LEFT JOIN FETCH koi.kitchenOrder ko
                LEFT JOIN FETCH ko.order o
                LEFT JOIN FETCH o.table
                LEFT JOIN FETCH o.branch
                LEFT JOIN FETCH o.items
                WHERE koi.kitchenStatus IN :statuses
            """)
    List<KitchenOrderItem> findActiveItems(
            @Param("statuses") List<KitchenStatus> statuses);
}