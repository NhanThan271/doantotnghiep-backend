package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.WarehouseImportRequest;
import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.WarehouseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final IngredientRepository ingredientRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final InventoryBatchRepository inventoryBatchRepository;

    @Override
    public Warehouse create(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    @Override
    public List<Warehouse> getAll() {
        return warehouseRepository.findAll();
    }

    @Override
    @Transactional
    public void importWarehouse(WarehouseImportRequest request) {

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        for (WarehouseImportRequest.Item item : request.getItems()) {

            Ingredient ingredient = ingredientRepository.findById(item.getIngredientId())
                    .orElseThrow(() -> new RuntimeException("Ingredient not found"));

            // cập nhật tồn kho tổng
            WarehouseInventory inventory = warehouseInventoryRepository
                    .findByWarehouseIdAndIngredientId(warehouse.getId(), ingredient.getId())
                    .orElse(null);

            if (inventory == null) {
                inventory = WarehouseInventory.builder()
                        .warehouse(warehouse)
                        .ingredient(ingredient)
                        .quantity(item.getQuantity())
                        .build();
            } else {
                inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            }

            warehouseInventoryRepository.save(inventory);

            // tạo batch
            InventoryBatch batch = InventoryBatch.builder()
                    .warehouse(warehouse)
                    .ingredient(ingredient)
                    .quantity(item.getQuantity())
                    .remainingQuantity(item.getQuantity())
                    .expiryDate(LocalDate.parse(item.getExpiryDate()))
                    .importedAt(LocalDateTime.now())
                    .build();

            inventoryBatchRepository.save(batch);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kho"));

        List<WarehouseInventory> inventory = warehouseInventoryRepository.findByWarehouseId(id);
        boolean hasStock = inventory.stream().anyMatch(i -> i.getQuantity() > 0);
        if (hasStock) {
            throw new RuntimeException("Không thể xóa kho còn hàng tồn");
        }

        warehouseInventoryRepository.deleteByWarehouseId(id);
        inventoryBatchRepository.deleteByWarehouseId(id);
        warehouseRepository.deleteById(id);
    }
}