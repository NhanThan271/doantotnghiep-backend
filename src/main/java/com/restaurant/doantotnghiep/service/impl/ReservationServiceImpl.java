package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.TableEntity;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.FoodRepository;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
import com.restaurant.doantotnghiep.repository.RoomRepository;
import com.restaurant.doantotnghiep.repository.TableRepository;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.service.ReservationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final TableRepository tableRepository;
    private final RoomRepository roomRepository;
    private final FoodRepository foodRepository;
    private final ReservationItemRepository reservationItemRepository;

    @Transactional
    public Reservation createFullReservation(Map<String, Object> request) {

        Long userId = Long.valueOf(request.get("userId").toString());
        Long branchId = Long.valueOf(request.get("branchId").toString());

        Long tableId = request.get("tableId") != null
                ? Long.valueOf(request.get("tableId").toString())
                : null;

        Long roomId = request.get("roomId") != null
                ? Long.valueOf(request.get("roomId").toString())
                : null;

        String reservationTime = request.get("reservationTime").toString();
        Double depositAmount = Double.valueOf(request.get("depositAmount").toString());

        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        TableEntity table = (tableId != null)
                ? tableRepository.findById(tableId).orElse(null)
                : null;

        Room room = (roomId != null)
                ? roomRepository.findById(roomId).orElse(null)
                : null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime time = LocalDateTime.parse(reservationTime, formatter);
        Reservation reservation = Reservation.builder()
                .user(user)
                .branch(branch)
                .table(table)
                .room(room)
                .reservationTime(time)
                .depositAmount(depositAmount)
                .totalPrice(0.0)
                .status(ReservationStatus.PENDING)
                .paymentStatus(PaymentStatus.UNPAID)
                .createdAt(LocalDateTime.now())
                .build();

        reservation = reservationRepository.save(reservation);

        double total = 0;

        for (Map<String, Object> item : items) {
            Long foodId = Long.valueOf(item.get("foodId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            Food food = foodRepository.findById(foodId)
                    .orElseThrow(() -> new RuntimeException("Food not found"));

            ReservationItem reservationItem = ReservationItem.builder()
                    .reservation(reservation)
                    .food(food)
                    .quantity(quantity)
                    .price(food.getPrice())
                    .build();

            reservationItemRepository.save(reservationItem);

            total += food.getPrice().doubleValue() * quantity;
        }

        reservation.setTotalPrice(total);

        return reservationRepository.save(reservation);
    }

    @Override
    public List<Reservation> getPendingReservations() {
        return reservationRepository.findByStatus(ReservationStatus.PENDING);
    }

    @Override
    public Reservation updateStatus(Long reservationId, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(status);
        reservation.setUpdatedAt(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }
}
