package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recipes", uniqueConstraints = @UniqueConstraint(columnNames = { "food_id", "ingredient_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantityRequired;
}
