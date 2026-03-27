package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

        // Tổng doanh thu của tất cả hóa đơn đã thanh toán
        @Query("""
                        SELECT SUM(b.totalAmount)
                        FROM Bill b
                        WHERE b.paymentStatus = com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus.PAID
                        """)
        Double getTotalRevenue();

        // Doanh thu theo từng ngày (ngày - tổng tiền)
        @Query("""
                        SELECT DATE(b.createdAt), SUM(b.totalAmount)
                        FROM Bill b
                        WHERE b.paymentStatus = com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus.PAID
                        GROUP BY DATE(b.createdAt)
                        ORDER BY DATE(b.createdAt)
                        """)
        List<Object[]> getDailyRevenue();

        // Doanh thu trong khoảng thời gian (từ ngày -> đến ngày)
        @Query("""
                        SELECT SUM(b.totalAmount)
                        FROM Bill b
                        WHERE b.paymentStatus = com.thantruongnhan.doanketthucmon.entity.enums.PaymentStatus.PAID
                        AND DATE(b.createdAt) BETWEEN :startDate AND :endDate
                        """)
        Double getRevenueByDateRange(
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Bill b WHERE b.order.id = :orderId")
        boolean existsByOrderId(@Param("orderId") Long orderId);

        Optional<Bill> findByOrderId(Long orderId);

        @Query("SELECT b FROM Bill b " +
                        "LEFT JOIN FETCH b.order o " +
                        "LEFT JOIN FETCH o.branch " +
                        "ORDER BY b.createdAt DESC")
        List<Bill> findAllWithOrder();
}
