package com.restaurant.doantotnghiep.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.restaurant.doantotnghiep.entity.Order;

@Controller
public class OrderWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(OrderWebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // WebSocket chỉ nhận đơn, không lưu DB
    @MessageMapping("/new-order")
    public void handleNewOrder(@Payload Order order) {
        log.info(" Nhận đơn mới qua WebSocket: {}", order);
        messagingTemplate.convertAndSend("/topic/orders", order);
    }

    // Các hàm gửi thông báo khác vẫn giữ nguyên
    public void sendNewOrder(Order order) {
        messagingTemplate.convertAndSend("/topic/orders", order);
    }

    public void sendOrderUpdate(Order order) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", order.getId());
        dto.put("status", order.getStatus());
        dto.put("updatedAt", order.getUpdatedAt());
        if (order.getTable() != null) {
            try {
                dto.put("tableNumber", order.getTable().getNumber());
            } catch (Exception ignored) {
            }
        }
        messagingTemplate.convertAndSend("/topic/orders", dto);
    }

    public void sendOrderDeleted(Long orderId) {
        messagingTemplate.convertAndSend("/topic/orders/deleted", orderId);
    }
}
