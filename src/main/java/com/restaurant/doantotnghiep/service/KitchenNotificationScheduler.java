package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
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

    @Scheduled(fixedRate = 60000) 
    public void notifyKitchen() {

        LocalDateTime from = LocalDateTime.now().plusMinutes(40);
        LocalDateTime to   = LocalDateTime.now().plusMinutes(41);

        List<Reservation> upcoming = reservationRepository
                .findByReservationTimeBetweenAndStatus(from, to, ReservationStatus.CONFIRMED);

        for (Reservation r : upcoming) {

            List<ReservationItem> items = reservationItemRepository
                    .findByReservationId(r.getId());

            List<Map<String, Object>> foodList = items.stream()
                    .map(item -> Map.<String, Object>of(
                            "foodName", item.getBranchFood().getFood().getName(),
                            "quantity", item.getQuantity()
                    ))
                    .collect(Collectors.toList());

            Map<String, Object> payload = new HashMap<>();
            payload.put("reservationId", "RES-" + r.getId());
            payload.put("customerName",  r.getCustomerName());
            payload.put("branch",        r.getBranch().getName());
            payload.put("table",         r.getTable() != null
                                            ? "Bàn " + r.getTable().getNumber()
                                            : r.getRoom() != null
                                                ? "Phòng " + r.getRoom().getNumber()
                                                : "Chưa xác định");
            payload.put("time",          r.getReservationTime()
                                            .format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")));
            payload.put("foods",         foodList);
            payload.put("message",       " Khách sẽ đến sau 40 phút, chuẩn bị món!");

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                restTemplate.postForEntity(
                    "http://localhost:3001/notify-kitchen-reservation",
                    entity,
                    Map.class
                );

                log.info("Notified kitchen for RES-{}", r.getId());

            } catch (Exception e) {
                log.error("Failed to notify kitchen: {}", e.getMessage());
            }
        }
    }
}