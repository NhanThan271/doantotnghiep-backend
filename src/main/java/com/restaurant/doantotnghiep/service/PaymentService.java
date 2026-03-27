package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.Payment;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.repository.OrderRepository;
import com.restaurant.doantotnghiep.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Payment createPayment(Long orderId, Payment payment) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Gắn thanh toán vào đơn hàng
        payment.setOrder(order);
        payment.setPaymentDate(LocalDateTime.now());

        // Cập nhật đơn hàng
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(OrderStatus.COMPLETED);
        order.setPayment(payment);

        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
