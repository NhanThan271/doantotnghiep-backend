package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.service.ReservationItemService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation-items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationItemController {

    private final ReservationItemService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','EMPLOYEE')")
    public ReservationItem create(
            @RequestParam Long reservationId,
            @RequestParam Long foodId,
            @RequestParam Integer quantity) {
        return service.create(reservationId, foodId, quantity);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','EMPLOYEE')")
    public ReservationItem update(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return service.update(id, quantity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','EMPLOYEE')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<ReservationItem> getByReservation(@PathVariable Long reservationId) {
        return service.getByReservation(reservationId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public ReservationItem getById(@PathVariable Long id) {
        return service.getById(id);
    }
}