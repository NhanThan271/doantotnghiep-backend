package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.dto.IngredientPreparationSummaryDTO;
import com.restaurant.doantotnghiep.service.IngredientPreparationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ingredient-preparation")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class IngredientPreparationController {

    private final IngredientPreparationService service;

    @GetMapping
    public ResponseEntity<IngredientPreparationSummaryDTO> calculate(
            @RequestParam Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        return ResponseEntity.ok(service.calculate(branchId, from, to));
    }
}