package com.restaurant.doantotnghiep.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_request_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequestItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnore
    private InventoryRequest request;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    @JsonIgnoreProperties({ "category" })
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;
}
