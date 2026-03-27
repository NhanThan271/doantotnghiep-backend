package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Lấy món chưa xong
    List<OrderItem> findByKitchenStatusNotOrderByCreatedAtAsc(KitchenStatus status);

    // Lấy món theo trạng thái
    List<OrderItem> findByKitchenStatusOrderByCreatedAtAsc(KitchenStatus status);

    // Kiểm tra order còn món chưa DONE không
    boolean existsByOrderIdAndKitchenStatusNot(Long orderId, KitchenStatus status);
}
