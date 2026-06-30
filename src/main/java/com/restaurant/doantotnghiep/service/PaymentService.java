package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.CashierSessionResponse;
import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.Payment;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.entity.Staff;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import com.restaurant.doantotnghiep.entity.enums.PaymentType;
import com.restaurant.doantotnghiep.repository.OrderRepository;
import com.restaurant.doantotnghiep.repository.PaymentRepository;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
import com.restaurant.doantotnghiep.repository.StaffRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReservationItemRepository reservationItemRepository;

    @Autowired
    private CashierSessionService cashierSessionService;

    @Autowired
    private StaffRepository staffRepository;

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
        Payment saved = paymentRepository.save(payment);

        try {
            User employee = order.getEmployee();
            if (employee != null) {
                Staff staff = staffRepository.findByUserId(employee.getId()).orElse(null);
                if (staff != null) {
                    CashierSessionResponse session = cashierSessionService.getCurrentSession(staff.getId());
                    if (session != null && saved.getTotalAmount() != null) {
                        cashierSessionService.updateRevenue(
                                session.getId(),
                                saved.getTotalAmount(),
                                "CASH");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi cập nhật ca thu ngân: " + e.getMessage());
        }

        return saved;
    }

    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Transactional
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
        Payment savedPayment = paymentRepository.save(payment);
        reservationRepository.save(reservation);

        if (reservation.getCustomerEmail() != null) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String tableInfo = "Không xác định";
            if (reservation.getTable() != null) {
                tableInfo = "Bàn " + reservation.getTable().getNumber();
            } else if (reservation.getRoom() != null) {
                tableInfo = "Phòng " + reservation.getRoom().getNumber();
            }

            List<ReservationItem> items = reservationItemRepository
                    .findByReservationId(reservation.getId());
            emailService.sendReservationPaymentEmail(
                    reservation.getCustomerEmail(),
                    reservation.getCustomerName(),
                    reservation.getBranch().getName(),
                    tableInfo,
                    reservation.getCheckInTime().format(formatter),
                    reservation.getCheckOutTime().format(formatter),
                    "RES" + reservation.getId(),
                    payment.getTotalAmount().doubleValue(),
                    items);
        }

        return savedPayment;
    }
}
