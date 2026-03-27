package com.restaurant.doantotnghiep.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.doantotnghiep.entity.enums.PaymentMethod;
import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bills")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({ "items", "employee", "table", "payment", "bill" })
    private Order order;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDateTime issuedAt;
    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}