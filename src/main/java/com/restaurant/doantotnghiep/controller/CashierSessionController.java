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
        public ResponseEntity<CashierSessionResponse> open(@RequestBody OpenSessionRequest request) {
                return ResponseEntity.ok(service.openSession(request));
        }

        @PostMapping("/{id}/close")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<CashierSessionResponse> close(
                        @PathVariable Long id,
                        @RequestBody CloseSessionRequest request) {
                return ResponseEntity.ok(service.closeSessionAndReturnResponse(id, request));
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<CashierSessionResponse> detail(@PathVariable Long id) {
                return ResponseEntity.ok(service.getByIdResponse(id));
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<List<CashierSessionResponse>> history() {
                return ResponseEntity.ok(service.getHistoryResponse());
        }

        @GetMapping("/current/{staffId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
        public ResponseEntity<CashierSessionResponse> current(@PathVariable Long staffId) {
                CashierSessionResponse response = service.getCurrentSession(staffId);
                if (response == null) {
                        return ResponseEntity.noContent().build();
                }
                return ResponseEntity.ok(response);
        }
}