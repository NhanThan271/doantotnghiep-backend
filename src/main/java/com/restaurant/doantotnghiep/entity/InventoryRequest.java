package com.restaurant.doantotnghiep.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.restaurant.doantotnghiep.entity.enums.RequestStatus;
import com.restaurant.doantotnghiep.entity.enums.RequestType;

@Entity
@Table(name = "inventory_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnoreProperties({ "ingredients", "tables", "promotions" })
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnoreProperties({ "inventories" })
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "requested_by_id")
    @JsonIgnoreProperties({ "password", "roles", "branches" })
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestType type;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @ManyToOne
    @JoinColumn(name = "approved_by_id")
    private User approvedBy;

    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;

    @PrePersist
    public void prePersist() {
        requestedAt = LocalDateTime.now();
    }
}
