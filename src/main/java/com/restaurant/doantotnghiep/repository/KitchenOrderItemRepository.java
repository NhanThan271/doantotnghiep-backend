package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.KitchenOrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("""
                SELECT koi FROM KitchenOrderItem koi
                LEFT JOIN FETCH koi.kitchenOrder ko
                LEFT JOIN FETCH ko.order o
                LEFT JOIN FETCH o.branch
                WHERE koi.id = :id
            """)
    Optional<KitchenOrderItem> findByIdWithDetails(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT k FROM KitchenOrderItem k WHERE k.id = :id")
    Optional<KitchenOrderItem> findByIdForUpdate(@Param("id") Long id);

}