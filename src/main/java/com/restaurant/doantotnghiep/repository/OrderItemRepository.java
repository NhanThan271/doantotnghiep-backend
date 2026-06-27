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

    @Query("""
                SELECT oi.food.id, oi.food.name, SUM(oi.quantity) as totalSold,
                       FUNCTION('WEEK', o.createdAt) as week,
                       FUNCTION('YEAR', o.createdAt) as year
                FROM OrderItem oi
                JOIN oi.order o
                WHERE o.status IN ('PAID', 'COMPLETED')
                  AND o.createdAt >= :from
                  AND o.createdAt <= :to
                  AND (:branchId IS NULL OR o.branch.id = :branchId)
                GROUP BY oi.food.id, oi.food.name,
                         FUNCTION('WEEK', o.createdAt),
                         FUNCTION('YEAR', o.createdAt)
                ORDER BY totalSold DESC
            """)
    List<Object[]> findWeeklySalesByFood(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("branchId") Long branchId);

    // Thêm query theo tháng
    @Query("""
                SELECT oi.food.id, oi.food.name, SUM(oi.quantity) as totalSold,
                       FUNCTION('MONTH', o.createdAt) as month,
                       FUNCTION('YEAR', o.createdAt) as year
                FROM OrderItem oi
                JOIN oi.order o
                WHERE o.status IN ('PAID', 'COMPLETED')
                  AND o.createdAt >= :from
                  AND (:branchId IS NULL OR o.branch.id = :branchId)
                GROUP BY oi.food.id, oi.food.name,
                         FUNCTION('MONTH', o.createdAt),
                         FUNCTION('YEAR', o.createdAt)
                ORDER BY year DESC, month DESC
            """)
    List<Object[]> findMonthlySalesByFood(
            @Param("from") LocalDateTime from,
            @Param("branchId") Long branchId);
}
