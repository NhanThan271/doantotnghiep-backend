package com.restaurant.doantotnghiep.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.doantotnghiep.entity.enums.RoomStatus;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(name = "uk_branch_room_number_area", columnNames = { "branch_id", "number", "area" })
})
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnoreProperties({"tables", "rooms", "promotions", "ingredients", "hibernateLazyInitializer", "handler"})
    private Branch branch;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private String area;

    @Column(nullable = false)
    private BigDecimal roomFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
