package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.Status;
import com.restaurant.doantotnghiep.service.RoomService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam Status status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<Room> getByBranch(@PathVariable Long branchId) {
        return service.getByBranch(branchId);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<Room> getByStatus(@RequestParam Status status) {
        return service.getByStatus(status);
    }

    @GetMapping("/available/{branchId}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public List<Room> getAvailable(@PathVariable Long branchId) {
        return service.getAvailableRooms(branchId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','MANAGER')")
    public Room getById(@PathVariable Long id) {
        return service.getById(id);
    }
}