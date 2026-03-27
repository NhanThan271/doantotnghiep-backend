package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {

    // Lấy theo order
    List<KitchenOrder> findByOrderId(Long orderId);

    // Lấy theo trạng thái bếp
    List<KitchenOrder> findByKitchenStatus(KitchenOrderStatus status);

    // Lấy danh sách đang làm (thực tế dùng nhiều)
    List<KitchenOrder> findByKitchenStatusIn(List<KitchenOrderStatus> statuses);
}