package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_inventory", uniqueConstraints = @UniqueConstraint(columnNames = {
        "warehouse_id", "ingredient_id"
}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;
}
