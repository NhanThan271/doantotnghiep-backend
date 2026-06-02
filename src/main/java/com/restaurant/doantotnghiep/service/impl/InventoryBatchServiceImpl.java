package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.InventoryBatchDTO;
import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.InventoryBatchService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class InventoryBatchServiceImpl implements InventoryBatchService {

    private final InventoryBatchRepository batchRepository;
    private final BranchRepository branchRepository;
    private final IngredientRepository ingredientRepository;
    private final BranchIngredientRepository branchIngredientRepository;

    public InventoryBatchServiceImpl(InventoryBatchRepository batchRepository,
            BranchRepository branchRepository,
            IngredientRepository ingredientRepository,
            BranchIngredientRepository branchIngredientRepository) {
        this.batchRepository = batchRepository;
        this.branchRepository = branchRepository;
        this.ingredientRepository = ingredientRepository;
        this.branchIngredientRepository = branchIngredientRepository;
    }

    @Override
    public InventoryBatch create(Long branchId, Long ingredientId,
            Double quantity, LocalDate expiryDate) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));

        // tạo batch
        InventoryBatch batch = InventoryBatch.builder()
                .branch(branch)
                .ingredient(ingredient)
                .quantity(quantity)
                .remainingQuantity(quantity)
                .expiryDate(expiryDate)
                .importedAt(LocalDateTime.now())
                .build();

        batchRepository.save(batch);

        // update tồn tổng chi nhánh - nguyên liệu
        BranchIngredient branchIngredient = branchIngredientRepository
                .findByBranchIdAndIngredientId(branchId, ingredientId)
                .orElse(null);

        if (branchIngredient == null) {
            branchIngredient = BranchIngredient.builder()
                    .branch(branch)
                    .ingredient(ingredient)
                    .quantity(quantity)
                    .build();
        } else {
            branchIngredient.setQuantity(
                    branchIngredient.getQuantity() + quantity);
        }

        branchIngredientRepository.save(branchIngredient);

        return batch;
    }

    @Override
    public InventoryBatch getById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    @Override
    public List<InventoryBatch> getAll() {
        return batchRepository.findAll();
    }

    @Override
    public List<InventoryBatch> getByBranchAndIngredient(Long branchId, Long ingredientId) {
        return batchRepository
                .findByBranchIdAndIngredientIdOrderByImportedAtAsc(branchId, ingredientId);
    }

    @Override
    public void delete(Long id) {
        InventoryBatch batch = getById(id);
        batchRepository.delete(batch);
    }

    @Override
    public List<InventoryBatchDTO> getByBranchAsDTO(Long branchId) {
        List<InventoryBatch> batches = batchRepository.findByBranchIdOrderByImportedAtDesc(branchId);

        return batches.stream().map(batch -> {
            Long days = batch.getExpiryDate() != null
                    ? java.time.temporal.ChronoUnit.DAYS.between(
                            LocalDate.now(), batch.getExpiryDate())
                    : null;

            return InventoryBatchDTO.builder()
                    .id(batch.getId())
                    .ingredientName(batch.getIngredient().getName())
                    .unit(batch.getIngredient().getUnit())
                    .quantity(batch.getQuantity())
                    .remainingQuantity(batch.getRemainingQuantity())
                    .createdAt(batch.getImportedAt())
                    .warehouseName(
                            batch.getWarehouse() != null
                                    ? batch.getWarehouse().getName()
                                    : null)
                    .daysToExpire(days) 
                    .expired(days != null && days < 0)
                    .nearExpired(days != null && days >= 0 && days <= 7)
                    .build();
        }).toList();
    }

    public List<InventoryBatch> getNearExpired() {
        return batchRepository.findByExpiryDateBetween(
                LocalDate.now(),
                LocalDate.now().plusDays(7));
    }

    public List<InventoryBatch> getWarehouseBatches(Long warehouseId) {
        return batchRepository
                .findByWarehouseIdOrderByExpiryDateAsc(warehouseId);
    }

    public List<Map<String, Object>> getAggregatedByWarehouse(Long warehouseId) {
        List<InventoryBatch> batches = batchRepository
                .findByWarehouseIdOrderByExpiryDateAsc(warehouseId);

        return batches.stream()
                .filter(b -> b.getExpiryDate() != null
                        && !b.getExpiryDate().isBefore(LocalDate.now())
                        && b.getRemainingQuantity() != null
                        && b.getRemainingQuantity() > 0)
                .collect(java.util.stream.Collectors.groupingBy(
                        b -> b.getIngredient().getId(),
                        java.util.stream.Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    InventoryBatch first = entry.getValue().get(0);
                    double total = entry.getValue().stream()
                            .mapToDouble(b -> b.getRemainingQuantity())
                            .sum();
                    Map<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("id", first.getIngredient().getId());
                    row.put("ingredient", Map.of(
                            "name", first.getIngredient().getName(),
                            "unit", first.getIngredient().getUnit()));
                    row.put("quantity", total);
                    return row;
                })
                .toList();
    }

    public List<Map<String, Object>> getAggregatedByBranch(Long branchId) {
        List<InventoryBatch> batches = batchRepository.findByBranchIdOrderByImportedAtDesc(branchId);

        return batches.stream()
                .filter(b -> b.getRemainingQuantity() != null && b.getRemainingQuantity() > 0)
                .collect(java.util.stream.Collectors.groupingBy(
                        b -> b.getIngredient().getId(),
                        java.util.stream.Collectors.toList()))
                .entrySet().stream()
                .map(entry -> {
                    InventoryBatch nearest = entry.getValue().stream()
                            .filter(b -> b.getExpiryDate() != null)
                            .min(java.util.Comparator.comparing(InventoryBatch::getExpiryDate))
                            .orElse(entry.getValue().get(0));

                    double validTotal = entry.getValue().stream()
                            .filter(b -> b.getExpiryDate() != null
                                    && !b.getExpiryDate().isBefore(LocalDate.now()))
                            .mapToDouble(InventoryBatch::getRemainingQuantity)
                            .sum();

                    double allTotal = entry.getValue().stream()
                            .mapToDouble(InventoryBatch::getRemainingQuantity)
                            .sum();

                    long daysToExpire = nearest.getExpiryDate() != null
                            ? java.time.temporal.ChronoUnit.DAYS.between(
                                    LocalDate.now(), nearest.getExpiryDate())
                            : 9999L;

                    Map<String, Object> row = new java.util.LinkedHashMap<>();
                    row.put("ingredientId", nearest.getIngredient().getId());
                    row.put("ingredientName", nearest.getIngredient().getName());
                    row.put("unit", nearest.getIngredient().getUnit());
                    row.put("totalQuantity", validTotal);
                    row.put("totalQuantityAll", allTotal);
                    row.put("nearestExpiryDate", nearest.getExpiryDate() != null
                            ? nearest.getExpiryDate().toString()
                            : null);
                    row.put("daysToExpire", daysToExpire);
                    row.put("expired", daysToExpire < 0);
                    row.put("nearExpired", daysToExpire >= 0 && daysToExpire <= 7);
                    return row;
                })
                .toList();
    }
}