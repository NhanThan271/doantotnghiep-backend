package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.service.CashierSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/cashier-sessions")
@RequiredArgsConstructor
public class CashierSessionController {

        private final CashierSessionService service;

        @PostMapping("/open")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> open(
                        @RequestBody OpenSessionRequest request) {
                return ResponseEntity.ok(
                                service.openSession(request));
        }

        @PostMapping("/{id}/close")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> close(
                        @PathVariable Long id,
                        @RequestBody CloseSessionRequest request) {
                return ResponseEntity.ok(
                                service.closeSession(id, request));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> detail(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                service.getById(id));
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> history() {
                return ResponseEntity.ok(
                                service.getHistory());
        }

        @GetMapping("/current/{staffId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> current(
                        @PathVariable Long staffId) {

                return ResponseEntity.ok(
                                service.getCurrentSession(staffId));
        }

}
