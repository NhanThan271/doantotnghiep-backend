package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branch_ingredients", uniqueConstraints = @UniqueConstraint(columnNames = { "branch_id",
        "ingredient_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private Double quantity;
}
