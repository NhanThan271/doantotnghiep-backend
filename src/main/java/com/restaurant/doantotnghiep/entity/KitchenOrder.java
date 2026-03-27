package com.restaurant.doantotnghiep.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kitchen_orders")
public class KitchenOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties({ "items", "payment" })
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "kitchen_status", nullable = false)
    private KitchenOrderStatus kitchenStatus = KitchenOrderStatus.WAITING;
}
