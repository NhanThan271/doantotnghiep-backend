package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.RoomStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {

    Room create(Long branchId, Integer number, Integer capacity, String area);

    Room update(Long id, Integer capacity, String area);

    Room updateStatus(Long id, RoomStatus status);

    void delete(Long id);

    Room getById(Long id);

    List<Room> getByBranch(Long branchId);

    List<Room> getByStatus(RoomStatus status);

    List<Room> getAvailableRooms(Long branchId, LocalDateTime checkIn, LocalDateTime checkOut);
}