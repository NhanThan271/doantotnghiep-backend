package com.restaurant.doantotnghiep.controller;

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

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public List<Reservation> getPendingReservations() {
        return reservationService.getPendingReservations();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN','EMPLOYEE')")
    public Reservation updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        return reservationService.updateStatus(id, status);
    }
}
