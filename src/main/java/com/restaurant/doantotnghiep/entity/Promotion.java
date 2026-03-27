package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "promotions")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(name = "promotion_foods", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "food_id"))
    @JsonIgnore
    @ToString.Exclude
    private Set<Food> foods;

    @JsonProperty("productIds")
    public List<Long> getProductIds() {
        if (foods == null) {
            return List.of();
        }
        return foods.stream()
                .map(Food::getId)
                .collect(Collectors.toList());
    }

    @ManyToMany
    @JoinTable(name = "promotion_branches", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "branch_id"))
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Set<Branch> branches;

    @JsonProperty("branchIds")
    public List<Long> getBranchIds() {
        if (branches == null) {
            return List.of();
        }
        return branches.stream()
                .map(Branch::getId)
                .collect(Collectors.toList());
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
