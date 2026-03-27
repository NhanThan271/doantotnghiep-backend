package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

        @Query("""
                            SELECT o FROM Order o
                            WHERE CAST(o.table.number AS string) LIKE CONCAT('%', :keyword, '%')
                               OR CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%')
                        """)
        List<Order> searchOrders(@Param("keyword") String keyword);

        @Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.status NOT IN ('PAID', 'CANCELED')")
        List<Order> findUnpaidOrdersByTableId(@Param("tableId") Long tableId);

        List<Order> findByTableIdAndStatusNotIn(Long id, List<OrderStatus> asList);

        @EntityGraph(attributePaths = {
                        "items",
                        "items.product",
                        "items.branchProduct",
                        "items.branchProduct.product",
                        "table",
                        "promotion",
                        "branch",
                        "employee"
        })
        @Query("SELECT DISTINCT o FROM Order o WHERE o.id = :id")
        Optional<Order> findWithItemsById(@Param("id") Long id);

        @Query("SELECT o FROM Order o " +
                        "LEFT JOIN FETCH o.branch " +
                        "LEFT JOIN FETCH o.items " +
                        "LEFT JOIN FETCH o.table " +
                        "LEFT JOIN FETCH o.employee")
        List<Order> findAllWithBranch();
}
