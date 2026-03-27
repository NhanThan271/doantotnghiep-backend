package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderId(Long orderId);
}
