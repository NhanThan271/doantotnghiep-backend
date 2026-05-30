package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.ReservationResponse;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.BranchFood;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;
import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.TableEntity;
import com.restaurant.doantotnghiep.entity.User;
import com.restaurant.doantotnghiep.entity.enums.PaymentStatus;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import com.restaurant.doantotnghiep.entity.enums.RoomStatus;
import com.restaurant.doantotnghiep.entity.enums.Status;
import com.restaurant.doantotnghiep.repository.BranchFoodRepository;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
import com.restaurant.doantotnghiep.repository.RoomRepository;
import com.restaurant.doantotnghiep.repository.TableRepository;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.service.KitchenOrderService;
import com.restaurant.doantotnghiep.service.ReservationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

        private final ReservationRepository reservationRepository;
        private final UserRepository userRepository;
        private final BranchRepository branchRepository;
        private final TableRepository tableRepository;
        private final RoomRepository roomRepository;
        private final BranchFoodRepository branchFoodRepository;
        private final ReservationItemRepository reservationItemRepository;
        private final KitchenOrderService kitchenOrderService;
        private final OrderServiceImpl orderService;

        @Transactional
        public Reservation createFullReservation(Map<String, Object> request) {

                Long userId = request.get("userId") != null
                                ? Long.valueOf(request.get("userId").toString())
                                : null;

                String customerName = request.get("customerName") != null
                                ? request.get("customerName").toString()
                                : null;

                String customerPhone = request.get("customerPhone") != null
                                ? request.get("customerPhone").toString()
                                : null;

                String customerEmail = request.get("customerEmail") != null
                                ? request.get("customerEmail").toString()
                                : null;

                Long branchId = Long.valueOf(request.get("branchId").toString());

                Long tableId = request.get("tableId") != null
                                ? Long.valueOf(request.get("tableId").toString())
                                : null;

                Long roomId = request.get("roomId") != null
                                ? Long.valueOf(request.get("roomId").toString())
                                : null;

                if (tableId == null && roomId == null) {
                        throw new RuntimeException(
                                        "Vui lòng chọn bàn hoặc phòng");
                }

                if (tableId != null && roomId != null) {
                        throw new RuntimeException(
                                        "Chỉ được chọn bàn hoặc phòng");
                }

                Double depositAmount = Double.valueOf(
                                request.get("depositAmount").toString());
                if (depositAmount < 0) {
                        throw new RuntimeException(
                                        "Tiền cọc không hợp lệ");
                }

                List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                LocalDateTime checkInTime = LocalDateTime.parse(
                                request.get("checkInTime").toString(),
                                formatter);

                LocalDateTime checkOutTime = LocalDateTime.parse(
                                request.get("checkOutTime").toString(),
                                formatter);
                if (!checkOutTime.isAfter(checkInTime)) {
                        throw new RuntimeException(
                                        "Thời gian trả phải sau thời gian nhận");
                }
                User user = null;

                if (userId != null) {
                        user = userRepository.findById(userId)
                                        .orElseThrow(() -> new RuntimeException("User not found"));
                }

                Branch branch = branchRepository.findById(branchId)
                                .orElseThrow(() -> new RuntimeException("Branch not found"));

                TableEntity table = (tableId != null)
                                ? tableRepository.findById(tableId)
                                                .orElseThrow(() -> new RuntimeException("Table not found"))
                                : null;

                Room room = (roomId != null)
                                ? roomRepository.findById(roomId)
                                                .orElseThrow(() -> new RuntimeException("Room not found"))
                                : null;

                if (room != null) {

                        boolean conflict = reservationRepository.existsRoomBookingConflict(
                                        roomId,
                                        checkInTime,
                                        checkOutTime);

                        if (conflict) {
                                throw new RuntimeException(
                                                "Phòng đã được đặt trong thời gian này");
                        }
                }

                if (table != null) {

                        boolean conflict = reservationRepository.existsTableBookingConflict(
                                        tableId,
                                        checkInTime,
                                        checkOutTime);

                        if (conflict) {
                                throw new RuntimeException(
                                                "Bàn đã được đặt trong thời gian này");
                        }
                }

                Reservation reservation = Reservation.builder()
                                .user(user)
                                .customerName(customerName)
                                .customerPhone(customerPhone)
                                .customerEmail(customerEmail)
                                .branch(branch)
                                .table(table)
                                .room(room)
                                .checkInTime(checkInTime)
                                .checkOutTime(checkOutTime)
                                .depositAmount(depositAmount)
                                .totalPrice(0.0)
                                .status(ReservationStatus.PENDING)
                                .paymentStatus(PaymentStatus.UNPAID)
                                .createdAt(LocalDateTime.now())
                                .build();

                reservation = reservationRepository.save(reservation);

                double total = 0;

                if (room != null) {
                        total += room.getRoomFee().doubleValue();
                }

                for (Map<String, Object> item : items) {

                        Long branchFoodId = Long.valueOf(item.get("branchFoodId").toString());

                        Integer quantity = Integer.valueOf(item.get("quantity").toString());

                        BranchFood branchFood = branchFoodRepository.findById(branchFoodId)
                                        .orElseThrow(() -> new RuntimeException("BranchFood not found"));

                        BigDecimal price = BigDecimal.valueOf(
                                        branchFood.getCustomPrice() != null
                                                        ? branchFood.getCustomPrice()
                                                        : branchFood.getFood().getPrice().doubleValue());

                        ReservationItem reservationItem = ReservationItem.builder()
                                        .reservation(reservation)
                                        .branchFood(branchFood)
                                        .quantity(quantity)
                                        .price(price)
                                        .build();

                        reservationItemRepository.save(reservationItem);

                        total += price.doubleValue() * quantity;
                }

                reservation.setTotalPrice(total);
                if (depositAmount > total) {
                        throw new RuntimeException(
                                        "Tiền cọc không được lớn hơn tổng tiền");
                }

                return reservationRepository.save(reservation);
        }

        @Override
        public List<ReservationResponse> getPendingReservations() {
                return reservationRepository.findByStatus(ReservationStatus.PENDING)
                                .stream()
                                .map(r -> ReservationResponse.builder()
                                                .id(r.getId())
                                                .userId(r.getUser() != null ? r.getUser().getId() : null)
                                                .customerName(r.getCustomerName())
                                                .phone(r.getCustomerPhone())
                                                .email(r.getCustomerEmail())
                                                .branchName(r.getBranch() != null ? r.getBranch().getName() : null)
                                                .tableNumber(
                                                                r.getTable() != null
                                                                                ? r.getTable().getNumber()
                                                                                : (Integer) null)
                                                .roomNumber(
                                                                r.getRoom() != null
                                                                                ? r.getRoom().getNumber()
                                                                                : (Integer) null)
                                                .status(r.getStatus().name())
                                                .checkInTime(r.getCheckInTime())
                                                .checkOutTime(r.getCheckOutTime())
                                                .remainingAmount(r.getTotalPrice() - r.getDepositAmount())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public Reservation updateStatus(Long id, ReservationStatus status) {
                Reservation reservation = reservationRepository.findByIdWithDetails(id)
                                .orElseThrow(() -> new RuntimeException("Reservation not found"));

                reservation.setStatus(status);
                reservation.setUpdatedAt(LocalDateTime.now());

                if (status == ReservationStatus.CHECKED_IN) {

                        if (reservation.getTable() != null) {

                                reservation.getTable().setStatus(Status.OCCUPIED);

                                tableRepository.save(reservation.getTable());
                        }

                        if (reservation.getRoom() != null) {

                                reservation.getRoom().setStatus(RoomStatus.OCCUPIED);

                                roomRepository.save(reservation.getRoom());
                        }

                        // tạo order nếu chưa có
                        if (reservation.getOrders() == null
                                        || reservation.getOrders().isEmpty()) {

                                orderService.createOrderFromReservation(
                                                reservation.getId());
                        }
                }

                if (status == ReservationStatus.COMPLETED) {

                        if (reservation.getPaymentStatus() != PaymentStatus.PAID) {
                                throw new RuntimeException(
                                                "Đơn đặt chưa thanh toán");
                        }

                        if (reservation.getTable() != null) {

                                reservation.getTable().setStatus(Status.FREE);

                                tableRepository.save(
                                                reservation.getTable());
                        }

                        if (reservation.getRoom() != null) {

                                reservation.getRoom().setStatus(RoomStatus.ACTIVE);

                                roomRepository.save(
                                                reservation.getRoom());
                        }
                }

                if (status == ReservationStatus.CANCELLED) {

                        if (reservation.getTable() != null) {

                                reservation.getTable().setStatus(Status.FREE);

                                tableRepository.save(
                                                reservation.getTable());
                        }

                        if (reservation.getRoom() != null) {

                                reservation.getRoom().setStatus(RoomStatus.ACTIVE);

                                roomRepository.save(
                                                reservation.getRoom());
                        }
                }
                return reservationRepository.save(reservation);
        }
}
