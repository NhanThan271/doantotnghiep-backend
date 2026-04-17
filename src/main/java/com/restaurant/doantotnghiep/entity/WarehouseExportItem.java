package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouse_export_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseExportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "export_id", nullable = false)
    private WarehouseExport export;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;
}