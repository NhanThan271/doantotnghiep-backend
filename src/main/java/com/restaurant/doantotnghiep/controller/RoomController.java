package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.RoomStatus;
import com.restaurant.doantotnghiep.service.RoomService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {

    private final RoomService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Room create(
            @RequestParam Long branchId,
            @RequestParam Integer number,
            @RequestParam Integer capacity,
            @RequestParam String area) {
        return service.create(branchId, number, capacity, area);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public Room update(
            @PathVariable Long id,
            @RequestParam Integer capacity,
            @RequestParam String area) {
        return service.update(id, capacity, area);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public Room updateStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/branch/{branchId}")
    public List<Room> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    @GetMapping("/status")
    public List<Room> getByStatus(@RequestParam RoomStatus status) {
        return service.getByStatus(status);
    }

    @GetMapping("/branch/{branchId}/available")
    public List<Room> getAvailableRooms(
            @PathVariable Long branchId,
            @RequestParam String checkIn,
            @RequestParam String checkOut) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return service.getAvailableRooms(
                branchId,
                LocalDateTime.parse(checkIn, fmt),
                LocalDateTime.parse(checkOut, fmt));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public Room getById(@PathVariable Long id) {
        return service.getById(id);
    }
}