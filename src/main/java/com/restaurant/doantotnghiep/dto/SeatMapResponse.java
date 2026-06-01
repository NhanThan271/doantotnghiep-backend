package com.restaurant.doantotnghiep.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatMapResponse {

    private Long id;

    private String type; // TABLE | ROOM

    private Integer number;

    private String area;

    private String status;

    private Boolean hasUpcomingReservation;

    private Long reservationId;

    private String customerName;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;
}
