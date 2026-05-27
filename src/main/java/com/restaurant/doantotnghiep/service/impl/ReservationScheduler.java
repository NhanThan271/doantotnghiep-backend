package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.service.KitchenOrderService;
import com.restaurant.doantotnghiep.service.ReservationService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    // Chạy mỗi 5 phút
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void autoConfirmUpcomingReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusHours(1); // sắp đến trong 1 tiếng

        List<Reservation> pendingList = reservationRepository
                .findByStatusAndCheckInTimeBefore(ReservationStatus.PENDING, threshold);

        for (Reservation r : pendingList) {
            reservationService.updateStatus(r.getId(), ReservationStatus.CONFIRMED);
        }
    }
}
