package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.Status;

import java.util.List;

public interface RoomService {

    Room create(Long branchId, Integer number, Integer capacity, String area);

    Room update(Long id, Integer capacity, String area);

    Room updateStatus(Long id, Status status);

    void delete(Long id);

    Room getById(Long id);

    List<Room> getByBranch(Long branchId);

    List<Room> getByStatus(Status status);

    List<Room> getAvailableRooms(Long branchId);
}