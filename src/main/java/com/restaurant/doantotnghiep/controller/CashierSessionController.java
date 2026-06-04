package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.CashierSessionResponse;
import com.restaurant.doantotnghiep.dto.CloseSessionRequest;
import com.restaurant.doantotnghiep.dto.OpenSessionRequest;
import com.restaurant.doantotnghiep.service.CashierSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

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
        public ResponseEntity<?> detail(@PathVariable Long id) {
                CashierSessionResponse response = service.getByIdResponse(id);
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> history() {
                List<CashierSessionResponse> responses = service.getHistoryResponse();
                return ResponseEntity.ok(responses);
        }

        @GetMapping("/current/{staffId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<?> current(
                        @PathVariable Long staffId) {

                return ResponseEntity.ok(
                                service.getCurrentSession(staffId));
        }

}