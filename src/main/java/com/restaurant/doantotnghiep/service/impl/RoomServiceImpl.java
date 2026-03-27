package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.entity.enums.Status;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;

    @Override
    public Room create(Long branchId, Integer number, Integer capacity, String area) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        // check trùng phòng
        roomRepository.findByBranchIdAndNumberAndArea(branchId, number, area)
                .ifPresent(r -> {
                    throw new RuntimeException("Room already exists in this branch");
                });

        Room room = Room.builder()
                .branch(branch)
                .number(number)
                .capacity(capacity)
                .area(area)
                .status(Status.FREE)
                .build();

        return roomRepository.save(room);
    }

    @Override
    public Room update(Long id, Integer capacity, String area) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setCapacity(capacity);
        room.setArea(area);

        return roomRepository.save(room);
    }

    @Override
    public Room updateStatus(Long id, Status status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStatus(status);

        return roomRepository.save(room);
    }

    @Override
    public void delete(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found");
        }
        roomRepository.deleteById(id);
    }

    @Override
    public Room getById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public List<Room> getByBranch(Long branchId) {
        return roomRepository.findByBranchId(branchId);
    }

    @Override
    public List<Room> getByStatus(Status status) {
        return roomRepository.findByStatus(status);
    }

    @Override
    public List<Room> getAvailableRooms(Long branchId) {
        return roomRepository.findByBranchIdAndStatus(branchId, Status.FREE);
    }
}