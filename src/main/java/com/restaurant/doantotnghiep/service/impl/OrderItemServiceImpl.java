package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.OrderItem;
import com.restaurant.doantotnghiep.repository.OrderItemRepository;
import com.restaurant.doantotnghiep.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public List<OrderItem> getAllItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public OrderItem getItemById(Long id) {
        return orderItemRepository.findById(id).orElse(null);
    }

    @Override
    public OrderItem createItem(OrderItem item) {
        return orderItemRepository.save(item);
    }

    @Override
    public OrderItem updateItem(Long id, OrderItem item) {
        OrderItem existing = orderItemRepository.findById(id).orElse(null);
        if (existing != null) {
            // set các field cần update, ví dụ:
            existing.setFood(item.getFood());
            existing.setQuantity(item.getQuantity());
            existing.setPrice(item.getPrice());
            return orderItemRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteItem(Long id) {
        orderItemRepository.deleteById(id);
    }
}
