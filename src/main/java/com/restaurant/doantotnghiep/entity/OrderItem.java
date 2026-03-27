package com.restaurant.doantotnghiep.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

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
@Table(name = "order_items")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({ "items", "payment" })
    private Order order;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Food food;

    @ManyToOne
    @JoinColumn(name = "branch_food_id")
    private BranchFood branchFood;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "kitchen_status", nullable = false)
    private KitchenStatus kitchenStatus = KitchenStatus.WAITING;

    @Column(nullable = false)
    private Boolean priority = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Tính subtotal
    public void calculateSubtotal() {
        if (price != null && quantity != null) {
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        calculateSubtotal();
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }
}