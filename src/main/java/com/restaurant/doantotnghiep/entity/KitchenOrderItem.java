package com.restaurant.doantotnghiep.entity;

import com.restaurant.doantotnghiep.entity.enums.KitchenStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "kitchen_order_items")
public class KitchenOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_order_id")
    private KitchenOrder kitchenOrder;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "food_id")
    // @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name = "kitchen_order_status", nullable = false)
    private KitchenStatus kitchenStatus = KitchenStatus.PREPARING;
}
