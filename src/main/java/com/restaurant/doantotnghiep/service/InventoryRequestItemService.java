package com.restaurant.doantotnghiep.service;

import java.util.List;

import com.restaurant.doantotnghiep.entity.InventoryRequestItem;

public interface InventoryRequestItemService {

    InventoryRequestItem addItem(Long requestId, Long ingredientId, Double quantity);

    InventoryRequestItem updateQuantity(Long itemId, Double quantity);

    void deleteItem(Long itemId);

    List<InventoryRequestItem> getItemsByRequest(Long requestId);
}
