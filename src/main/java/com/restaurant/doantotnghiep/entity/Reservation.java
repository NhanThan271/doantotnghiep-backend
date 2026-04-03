package com.restaurant.doantotnghiep.entity;

import java.time.LocalDateTime;

import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private TableEntity table;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private Double totalPrice;

    @Column(nullable = false)
    private Double depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
