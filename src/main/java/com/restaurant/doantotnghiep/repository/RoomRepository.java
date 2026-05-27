package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByBranchId(Long branchId);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByBranchIdAndStatus(Long branchId, RoomStatus status);

    Optional<Room> findByBranchIdAndNumberAndArea(Long branchId, Integer number, String area);

    @Query("""
                SELECT r FROM Room r
                WHERE r.branch.id = :branchId
                  AND r.status = 'ACTIVE'
                  AND r.id NOT IN (
                      SELECT res.room.id FROM Reservation res
                      WHERE res.room IS NOT NULL
                        AND res.status <> 'CANCELLED'
                        AND (:checkIn < res.checkOutTime AND :checkOut > res.checkInTime)
                  )
            """)
    List<Room> findAvailableRooms(
            @Param("branchId") Long branchId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);
}