package com.restaurant.doantotnghiep.dto;

import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private Long userId;
    private String customerName;
    private String phone;
    private String email;
    private String branchName;
    private Integer tableNumber;
    private Integer roomNumber;
    private String area;
    private Double remainingAmount;
    private String status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Double depositAmount;
    private Double totalPrice;
}
