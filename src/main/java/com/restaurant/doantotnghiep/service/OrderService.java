package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.enums.OrderStatus;
import com.restaurant.doantotnghiep.entity.enums.PaymentMethod;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order createOrder(Order order);

    Order updateOrder(Long id, OrderStatus status, PaymentMethod paymentMethod);

    void deleteOrder(Long id);

    Order getOrderById(Long id);

    List<Order> getAllOrders();

    Order addFoodToOrder(Long orderId, Food food, int quantity);

    List<Order> searchOrders(String keyword);

    Order updateOrderStatus(Long id, OrderStatus status);

    Order addMultipleFoodsToOrder(Long orderId, List<Map<String, Object>> newItems);

}
