package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Order;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
import com.restaurant.doantotnghiep.service.impl.OrderServiceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class KitchenNotificationScheduler {

        private final ReservationRepository reservationRepository;
        private final ReservationItemRepository reservationItemRepository;
        private final RestTemplate restTemplate;
        private final OrderServiceImpl orderService;

        @Transactional
        @Scheduled(fixedRate = 5000)
        public void notifyKitchen() {

                LocalDateTime from = LocalDateTime.now().plusMinutes(40);
                LocalDateTime to = LocalDateTime.now().plusMinutes(50);

                List<Reservation> upcoming = reservationRepository
                                .findByCheckInTimeBetweenAndStatus(from, to, ReservationStatus.CONFIRMED);

                for (Reservation r : upcoming) {

                        if (Boolean.TRUE.equals(r.getNotifiedKitchen())) {
                                continue;
                        }

                        List<ReservationItem> items = reservationItemRepository
                                        .findByReservationId(r.getId());

                        if (items.isEmpty()) {
                                log.info("RES-{} không có món đặt trước, bỏ qua", r.getId());
                                continue;
                        }

                        try {
                                Order createdOrder = orderService.createOrderFromReservation(r.getId());
                                r.setNotifiedKitchen(true);

                                List<Map<String, Object>> kitchenItems = items.stream()
                                                .map(item -> Map.<String, Object>of(
                                                                "id", item.getBranchFood().getFood().getId(),
                                                                "name", item.getBranchFood().getFood().getName(),
                                                                "quantity", item.getQuantity()))
                                                .collect(Collectors.toList());

                                Map<String, Object> orderPayload = new HashMap<>();
                                orderPayload.put("orderId", createdOrder.getId());
                                orderPayload.put("branchId", r.getBranch().getId());
                                orderPayload.put("tableNumber",
                                                r.getTable() != null ? r.getTable().getNumber()
                                                                : r.getRoom() != null ? r.getRoom().getNumber() : "");
                                orderPayload.put("locationName",
                                                r.getTable() != null ? "Bàn " + r.getTable().getNumber()
                                                                : r.getRoom() != null
                                                                                ? "Phòng " + r.getRoom().getNumber()
                                                                                : "");
                                orderPayload.put("areaName",
                                                r.getTable() != null
                                                                ? (r.getTable().getArea() != null
                                                                                ? r.getTable().getArea()
                                                                                : "Khu chính")
                                                                : r.getRoom() != null ? "Khu VIP" : "Khu chính");
                                orderPayload.put("items", kitchenItems);
                                orderPayload.put("isRoom", r.getRoom() != null);
                                orderPayload.put("timestamp", java.time.LocalDateTime.now().toString());

                                try {
                                        HttpHeaders orderHeaders = new HttpHeaders();
                                        orderHeaders.setContentType(MediaType.APPLICATION_JSON);
                                        HttpEntity<Map<String, Object>> orderEntity = new HttpEntity<>(orderPayload,
                                                        orderHeaders);
                                        restTemplate.postForEntity(
                                                        "http://localhost:3001/notify-new-order",
                                                        orderEntity,
                                                        Map.class);
                                        log.info("Đã emit new-order cho bếp, orderId={}", createdOrder.getId());
                                } catch (Exception e) {
                                        log.error("Không thể emit new-order: {}", e.getMessage());
                                }

                        } catch (Exception e) {
                                log.error("Lỗi tạo kitchen order cho RES-{}: {}", r.getId(), e.getMessage());
                                continue;
                        }

                        List<Map<String, Object>> foodList = items.stream()
                                        .map(item -> Map.<String, Object>of(
                                                        "foodName", item.getBranchFood().getFood().getName(),
                                                        "quantity", item.getQuantity()))
                                        .collect(Collectors.toList());

                        Map<String, Object> payload = new HashMap<>();
                        payload.put("reservationId", "RES-" + r.getId());
                        payload.put("customerName", r.getCustomerName());
                        payload.put(
                                        "branchId",
                                        r.getBranch().getId());
                        payload.put("branch", r.getBranch().getName());
                        payload.put("table", r.getTable() != null
                                        ? "Bàn " + r.getTable().getNumber()
                                        : r.getRoom() != null
                                                        ? "Phòng " + r.getRoom().getNumber()
                                                        : "Chưa xác định");
                        payload.put("time", r.getCheckInTime()
                                        .format(DateTimeFormatter.ofPattern("HH:mm")));
                        payload.put("checkInTime", r.getCheckInTime()
                                        .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));

                        payload.put("checkOutTime", r.getCheckOutTime()
                                        .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
                        payload.put("foods", foodList);
                        payload.put("message", " Khách sẽ đến sau 40 phút, chuẩn bị món!");

                        try {
                                HttpHeaders headers = new HttpHeaders();
                                headers.setContentType(MediaType.APPLICATION_JSON);
                                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                                restTemplate.postForEntity(
                                                "http://localhost:3001/notify-kitchen-reservation",
                                                entity,
                                                Map.class);

                                reservationRepository.save(r);

                                log.info("Notified kitchen for RES-{}", r.getId());

                        } catch (Exception e) {
                                log.error("Failed to notify kitchen: {}", e.getMessage());
                        }
                }
        }
}