package com.restaurant.doantotnghiep.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.restaurant.doantotnghiep.entity.InventoryRequestItem;

public interface InventoryRequestItemRepository
        extends JpaRepository<InventoryRequestItem, Long> {

    List<InventoryRequestItem> findByRequestId(Long requestId);

    boolean existsByRequestIdAndIngredientId(Long requestId, Long ingredientId);
}
