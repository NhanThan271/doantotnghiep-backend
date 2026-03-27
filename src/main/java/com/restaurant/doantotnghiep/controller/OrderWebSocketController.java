package com.restaurant.doantotnghiep.controller;

import com.restaurant.doantotnghiep.entity.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

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
        messagingTemplate.convertAndSend("/topic/orders/update", order);
    }

    public void sendOrderDeleted(Long orderId) {
        messagingTemplate.convertAndSend("/topic/orders/deleted", orderId);
    }
}
