package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.ReservationResponse;
import com.restaurant.doantotnghiep.dto.SeatMapResponse;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.BranchFood;
import com.restaurant.doantotnghiep.entity.Order;
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
import com.restaurant.doantotnghiep.repository.OrderRepository;
import com.restaurant.doantotnghiep.repository.ReservationItemRepository;
import com.restaurant.doantotnghiep.repository.ReservationRepository;
import com.restaurant.doantotnghiep.repository.RoomRepository;
import com.restaurant.doantotnghiep.repository.TableRepository;
import com.restaurant.doantotnghiep.repository.UserRepository;
import com.restaurant.doantotnghiep.service.EmailService;
import com.restaurant.doantotnghiep.service.PriceCalculationService;
import com.restaurant.doantotnghiep.service.ReservationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
        private final EmailService emailService;
        private final OrderServiceImpl orderService;
        private final PriceCalculationService priceCalculationService;
        private final OrderRepository orderRepository;
        private final RestTemplate restTemplate;
        private final ReservationItemRepository reservationItemRepository;

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

                        BigDecimal price;
                        Object rawPrice = item.get("price");
                        if (rawPrice != null) {
                                price = new BigDecimal(rawPrice.toString());
                        } else {
                                price = priceCalculationService.calculateFinalPrice(branchFood);
                        }

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

                Reservation savedReservation = reservationRepository.save(reservation);

                sendReservationCreatedEmail(savedReservation);

                return savedReservation;
        }

        private void sendReservationCreatedEmail(Reservation reservation) {

                if (reservation.getCustomerEmail() == null
                                || reservation.getCustomerEmail().isBlank()) {
                        return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                String seatInfo = "";

                if (reservation.getTable() != null) {
                        seatInfo = "Bàn " + reservation.getTable().getNumber();
                }

                if (reservation.getRoom() != null) {
                        seatInfo = "Phòng " + reservation.getRoom().getNumber();
                }

                List<ReservationItem> items = reservationItemRepository.findByReservationId(
                                reservation.getId());

                emailService.sendReservationCreatedEmail(
                                reservation.getCustomerEmail(),
                                reservation.getCustomerName(),
                                reservation.getBranch().getName(),
                                seatInfo,
                                reservation.getCheckInTime().format(formatter),
                                reservation.getCheckOutTime().format(formatter),
                                "RES" + reservation.getId(),
                                items);
        }

        @Override
        public List<ReservationResponse> getReservationsByStatus(ReservationStatus status) {
                List<Reservation> list = (status == null)
                                ? reservationRepository.findAll()
                                : reservationRepository.findByStatus(status);

                return list.stream()
                                .map(r -> ReservationResponse.builder()
                                                .id(r.getId())
                                                .userId(r.getUser() != null ? r.getUser().getId() : null)
                                                .customerName(r.getCustomerName())
                                                .phone(r.getCustomerPhone())
                                                .email(r.getCustomerEmail())
                                                .branchName(r.getBranch() != null ? r.getBranch().getName() : null)
                                                .tableNumber(r.getTable() != null ? r.getTable().getNumber() : null)
                                                .roomNumber(r.getRoom() != null ? r.getRoom().getNumber() : null)
                                                .area(
                                                                r.getTable() != null ? r.getTable().getArea()
                                                                                : r.getRoom() != null
                                                                                                ? r.getRoom().getArea()
                                                                                                : null)
                                                .status(r.getStatus().name())
                                                .checkInTime(r.getCheckInTime())
                                                .checkOutTime(r.getCheckOutTime())
                                                .totalPrice(r.getTotalPrice())
                                                .depositAmount(r.getDepositAmount())
                                                .remainingAmount(calculateRemainingAmount(r))
                                                .build())
                                .collect(Collectors.toList());
        }

        private double calculateRemainingAmount(Reservation r) {
                if (r.getPaymentStatus() == PaymentStatus.PAID) {
                        return 0.0;
                }
                double total = Math.round(r.getTotalPrice() * 100.0) / 100.0;
                double deposit = Math.round(r.getDepositAmount() * 100.0) / 100.0;
                double remaining = Math.round((total - deposit) * 100.0) / 100.0;

                return Math.max(0.0, remaining);
        }

        @Override
        @Transactional
        public Reservation updateStatus(Long id, ReservationStatus status) {
                Reservation reservation = reservationRepository.findByIdWithDetails(id)
                                .orElseThrow(() -> new RuntimeException("Reservation not found"));

                reservation.setStatus(status);
                reservation.setUpdatedAt(LocalDateTime.now());
                if (status == ReservationStatus.CONFIRMED) {
                        boolean orderExists = orderRepository.existsByReservationId(reservation.getId());
                        if (!orderExists) {
                                Order createdOrder = orderService.createOrderFromReservation(reservation.getId());
                                long minutesUntilCheckIn = java.time.Duration.between(
                                                LocalDateTime.now(),
                                                reservation.getCheckInTime()).toMinutes();

                                if (minutesUntilCheckIn <= 40) {
                                        notifyKitchenViaSocket(reservation, createdOrder);
                                }
                        }
                }

                if (status == ReservationStatus.CHECKED_IN) {
                        if (reservation.getTable() != null) {
                                reservation.getTable().setStatus(Status.OCCUPIED);
                                tableRepository.save(reservation.getTable());
                        }
                        if (reservation.getRoom() != null) {
                                reservation.getRoom().setStatus(RoomStatus.OCCUPIED);
                                roomRepository.save(reservation.getRoom());
                        }
                        boolean orderExists = orderRepository.existsByReservationId(reservation.getId());
                        if (!orderExists) {
                                orderService.createOrderFromReservation(reservation.getId());
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

        public List<SeatMapResponse> getTableMap() {
                try {
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime fourHoursLater = now.plusHours(4);

                        List<Reservation> reservations = reservationRepository
                                        .findUpcomingReservations(now, fourHoursLater);

                        // Sửa: Dùng Map với merge function để tránh duplicate key
                        Map<Long, Reservation> reservationMap = new HashMap<>();
                        if (reservations != null) {
                                for (Reservation r : reservations) {
                                        if (r.getTable() != null) {
                                                reservationMap.put(r.getTable().getId(), r);
                                        }
                                }
                        }

                        List<TableEntity> allTables = tableRepository.findAll();
                        if (allTables == null) {
                                return new ArrayList<>();
                        }

                        List<SeatMapResponse> result = new ArrayList<>();
                        for (TableEntity table : allTables) {
                                try {
                                        Reservation reservation = reservationMap.get(table.getId());

                                        // Xử lý status an toàn, tránh NullPointerException
                                        String status = "FREE";
                                        if (table.getStatus() != null) {
                                                status = table.getStatus().name();
                                        }

                                        // Nếu có reservation CONFIRMED trong tương lai, đánh dấu là RESERVED
                                        if (reservation != null && "FREE".equals(status)) {
                                                status = "RESERVED";
                                        }

                                        SeatMapResponse response = SeatMapResponse.builder()
                                                        .id(table.getId())
                                                        .type("TABLE")
                                                        .number(table.getNumber())
                                                        .area(table.getArea())
                                                        .status(status)
                                                        .hasUpcomingReservation(reservation != null)
                                                        .reservationId(reservation != null ? reservation.getId() : null)
                                                        .customerName(reservation != null
                                                                        ? reservation.getCustomerName()
                                                                        : null)
                                                        .checkInTime(reservation != null ? reservation.getCheckInTime()
                                                                        : null)
                                                        .checkOutTime(reservation != null
                                                                        ? reservation.getCheckOutTime()
                                                                        : null)
                                                        .build();
                                        result.add(response);
                                } catch (Exception e) {
                                        // Log lỗi nhưng không dừng toàn bộ
                                        System.err.println(
                                                        "Error mapping table " + table.getId() + ": " + e.getMessage());
                                }
                        }
                        return result;
                } catch (Exception e) {
                        System.err.println("Error in getTableMap: " + e.getMessage());
                        e.printStackTrace();
                        return new ArrayList<>();
                }
        }

        public List<SeatMapResponse> getRoomMap() {

                LocalDateTime now = LocalDateTime.now();

                LocalDateTime fourHoursLater = now.plusHours(4);

                List<Reservation> reservations = reservationRepository
                                .findUpcomingReservations(
                                                now,
                                                fourHoursLater);

                Map<Long, Reservation> reservationMap = reservations.stream()
                                .filter(r -> r.getRoom() != null)
                                .collect(Collectors.toMap(
                                                r -> r.getRoom().getId(),
                                                r -> r));

                return roomRepository.findAll()
                                .stream()
                                .map(room -> {

                                        Reservation reservation = reservationMap.get(room.getId());

                                        return SeatMapResponse.builder()
                                                        .id(room.getId())
                                                        .type("ROOM")
                                                        .number(room.getNumber())
                                                        .area(room.getArea())
                                                        .status(room.getStatus().name())
                                                        .hasUpcomingReservation(
                                                                        reservation != null)
                                                        .reservationId(
                                                                        reservation != null
                                                                                        ? reservation.getId()
                                                                                        : null)
                                                        .customerName(
                                                                        reservation != null
                                                                                        ? reservation.getCustomerName()
                                                                                        : null)
                                                        .checkInTime(
                                                                        reservation != null
                                                                                        ? reservation.getCheckInTime()
                                                                                        : null)
                                                        .checkOutTime(
                                                                        reservation != null
                                                                                        ? reservation.getCheckOutTime()
                                                                                        : null)
                                                        .build();
                                })
                                .toList();
        }

        private void notifyKitchenViaSocket(Reservation reservation, Order createdOrder) {
                try {
                        List<ReservationItem> items = reservationItemRepository
                                        .findByReservationId(reservation.getId());

                        List<Map<String, Object>> kitchenItems = items.stream()
                                        .map(item -> Map.<String, Object>of(
                                                        "id", item.getBranchFood().getFood().getId(),
                                                        "name", item.getBranchFood().getFood().getName(),
                                                        "quantity", item.getQuantity()))
                                        .collect(Collectors.toList());

                        Map<String, Object> payload = new HashMap<>();
                        payload.put("orderId", createdOrder.getId());
                        payload.put("branchId", reservation.getBranch().getId());
                        payload.put("tableNumber",
                                        reservation.getTable() != null ? reservation.getTable().getNumber()
                                                        : reservation.getRoom() != null
                                                                        ? reservation.getRoom().getNumber()
                                                                        : "");
                        payload.put("locationName",
                                        reservation.getTable() != null ? "Bàn " + reservation.getTable().getNumber()
                                                        : reservation.getRoom() != null
                                                                        ? "Phòng " + reservation.getRoom().getNumber()
                                                                        : "");
                        payload.put("areaName",
                                        reservation.getTable() != null
                                                        ? (reservation.getTable().getArea() != null
                                                                        ? reservation.getTable().getArea()
                                                                        : "Khu chính")
                                                        : reservation.getRoom() != null ? "Khu VIP" : "Khu chính");
                        payload.put("items", kitchenItems);
                        payload.put("isRoom", reservation.getRoom() != null);
                        payload.put("timestamp", LocalDateTime.now().toString());

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

                        restTemplate.postForEntity(
                                        "http://localhost:3001/notify-new-order",
                                        entity,
                                        Map.class);

                } catch (Exception e) {
                        System.err.println("Không thể notify kitchen qua socket: " + e.getMessage());
                }
        }
}
