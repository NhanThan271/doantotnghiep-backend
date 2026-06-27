package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Lấy món chưa xong
    List<OrderItem> findByKitchenStatusNotOrderByCreatedAtAsc(KitchenStatus status);

    // Lấy món theo trạng thái
    List<OrderItem> findByKitchenStatusOrderByCreatedAtAsc(KitchenStatus status);

    // Kiểm tra order còn món chưa DONE không
    boolean existsByOrderIdAndKitchenStatusNot(Long orderId, KitchenStatus status);

    @Query(value = """
                SELECT oi.food_id,
                       f.name,
                       SUM(oi.quantity)      AS totalSold,
                       WEEK(o.created_at)    AS week,
                       YEAR(o.created_at)    AS year
                FROM order_items oi
                JOIN orders o ON o.id = oi.order_id
                JOIN foods  f ON f.id = oi.food_id
                WHERE o.status IN ('PAID', 'COMPLETED')
                  AND o.created_at >= :from
                  AND o.created_at <= :to
                  AND (:branchId IS NULL OR o.branch_id = :branchId)
                GROUP BY oi.food_id, f.name, WEEK(o.created_at), YEAR(o.created_at)
                ORDER BY YEAR(o.created_at) ASC, WEEK(o.created_at) ASC
            """, nativeQuery = true)
    List<Object[]> findWeeklySalesByFood(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("branchId") Long branchId);

    @Query(value = """
                SELECT oi.food_id,
                       f.name,
                       SUM(oi.quantity)       AS totalSold,
                       MONTH(o.created_at)    AS month,
                       YEAR(o.created_at)     AS year
                FROM order_items oi
                JOIN orders o ON o.id = oi.order_id
                JOIN foods  f ON f.id = oi.food_id
                WHERE o.status IN ('PAID', 'COMPLETED')
                  AND o.created_at >= :from
                  AND (:branchId IS NULL OR o.branch_id = :branchId)
                GROUP BY oi.food_id, f.name, MONTH(o.created_at), YEAR(o.created_at)
                ORDER BY YEAR(o.created_at) ASC, MONTH(o.created_at) ASC
            """, nativeQuery = true)
    List<Object[]> findMonthlySalesByFood(
            @Param("from") LocalDateTime from,
            @Param("branchId") Long branchId);
}
