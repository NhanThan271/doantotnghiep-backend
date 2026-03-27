package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByBranchId(Long branchId);

    List<Room> findByStatus(Status status);

    List<Room> findByBranchIdAndStatus(Long branchId, Status status);

    Optional<Room> findByBranchIdAndNumberAndArea(Long branchId, Integer number, String area);
}