package com.restaurant.doantotnghiep.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.repository.ReservationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ReservationNotificationScheduler {
    private final ReservationRepository reservationRepository;
    private final RestTemplate restTemplate;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void notifyCashierAndStaff() {

        LocalDateTime from = LocalDateTime.now().plusHours(4);

        LocalDateTime to = from.plusMinutes(5);

        List<Reservation> reservations = reservationRepository
                .findByCheckInTimeBetweenAndStatus(
                        from,
                        to,
                        ReservationStatus.CONFIRMED);

        for (Reservation reservation : reservations) {

            if (Boolean.TRUE.equals(
                    reservation.getNotifiedFourHours())) {
                continue;
            }

            String location;

            if (reservation.getTable() != null) {

                location = "Bàn "
                        + reservation.getTable().getNumber();

            } else {

                location = "Phòng "
                        + reservation.getRoom().getNumber();
            }

            String message = "Khách "
                    + reservation.getCustomerName()
                    + " sẽ đến lúc "
                    + reservation.getCheckInTime()
                    + " tại "
                    + location;

            Map<String, Object> payload = new HashMap<>();

            payload.put("type", "UPCOMING_RESERVATION");
            payload.put("message", message);
            payload.put("reservationId", reservation.getId());
            payload.put("branchId",
                    reservation.getBranch().getId());

            payload.put("customerName",
                    reservation.getCustomerName());

            payload.put("checkInTime",
                    reservation.getCheckInTime());

            payload.put("location",
                    location);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(
                    "http://localhost:3001/notify-staff-reservation",
                    entity,
                    Map.class);

            reservation.setNotifiedFourHours(true);
        }

        reservationRepository.saveAll(reservations);
    }
}
