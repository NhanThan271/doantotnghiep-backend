package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.service.CashierSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cashier-sessions")
@RequiredArgsConstructor
public class CashierSessionController {

    private final CashierSessionService service;

    @PostMapping("/open")
    public ResponseEntity<?> open(
            @RequestBody OpenSessionRequest request) {
        return ResponseEntity.ok(
                service.openSession(request));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> close(
            @PathVariable Long id,
            @RequestBody CloseSessionRequest request) {
        return ResponseEntity.ok(
                service.closeSession(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                service.getById(id));
    }

    @GetMapping
    public ResponseEntity<?> history() {
        return ResponseEntity.ok(
                service.getHistory());
    }

    @GetMapping("/current/{staffId}")
    public ResponseEntity<?> current(
            @PathVariable Long staffId) {

        return ResponseEntity.ok(
                service.getCurrentSession(staffId));
    }

}
