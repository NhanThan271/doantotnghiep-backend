package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.ReservationResponse;
import com.restaurant.doantotnghiep.dto.SeatMapResponse;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.service.ReservationService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/full")
    public Reservation createFull(@RequestBody Map<String, Object> request) {
        return reservationService.createFullReservation(request);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public List<ReservationResponse> getReservationsByStatus(
            @RequestParam(required = false) ReservationStatus status) {
        return reservationService.getReservationsByStatus(status);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public Reservation updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        return reservationService.updateStatus(id, status);
    }

    @GetMapping("/tables")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public List<SeatMapResponse> getTables() {
        return reservationService.getTableMap();
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public List<SeatMapResponse> getRooms() {
        return reservationService.getRoomMap();
    }
}
