package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.Payment;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import com.restaurant.doantotnghiep.entity.enums.PaymentType;
import com.restaurant.doantotnghiep.repository.OrderRepository;
import com.restaurant.doantotnghiep.repository.PaymentRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReservationRepository reservationRepository;

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

    public Payment createReservationPayment(Long reservationId, Payment payment) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Reservation already paid");
        }

        payment.setReservation(reservation);
        payment.setOrder(null);
        payment.setPaymentDate(LocalDateTime.now());

        // Thanh toán cọc
        if (payment.getPaymentType() == PaymentType.DEPOSIT) {

            if (reservation.getPaymentStatus() == PaymentStatus.UNPAID) {
                payment.setTotalAmount(
                        BigDecimal.valueOf(reservation.getDepositAmount()));
                reservation.setPaymentStatus(PaymentStatus.PARTIAL);
            } else {
                throw new RuntimeException("Already deposited");
            }
        }

        // Thanh toán full
        else if (payment.getPaymentType() == PaymentType.FULL) {

            // Nếu chưa cọc
            if (reservation.getPaymentStatus() == PaymentStatus.UNPAID) {

                payment.setTotalAmount(
                        BigDecimal.valueOf(reservation.getTotalPrice()));

            }

            // Nếu đã cọc rồi thì trả phần còn lại
            else if (reservation.getPaymentStatus() == PaymentStatus.PARTIAL) {

                double remaining = reservation.getTotalPrice() - reservation.getDepositAmount();

                payment.setTotalAmount(BigDecimal.valueOf(remaining));
            }

            reservation.setPaymentStatus(PaymentStatus.PAID);
        }

        payment.setStatus("SUCCESS");

        reservationRepository.save(reservation);
        return paymentRepository.save(payment);
    }
}
