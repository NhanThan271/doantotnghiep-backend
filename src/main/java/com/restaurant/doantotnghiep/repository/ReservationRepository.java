package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.Room;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByBranchId(Long branchId);

    List<Reservation> findByCheckInTimeBetweenAndStatus(
            LocalDateTime from,
            LocalDateTime to,
            ReservationStatus status);

    List<Reservation> findByStatusAndCheckInTimeBefore(
            ReservationStatus status,
            LocalDateTime time);

    @Query("""
                SELECT COUNT(r) > 0
                FROM Reservation r
                WHERE r.room.id = :roomId
                  AND r.status <> 'CANCELLED'
                  AND (:checkIn < r.checkOutTime AND :checkOut > r.checkInTime)
            """)
    boolean existsRoomBookingConflict(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);

    @Query("""
                SELECT COUNT(r) > 0
                FROM Reservation r
                WHERE r.table.id = :tableId
                  AND r.status <> 'CANCELLED'
                  AND (:checkIn < r.checkOutTime
                   AND :checkOut > r.checkInTime)
            """)
    boolean existsTableBookingConflict(
            @Param("tableId") Long tableId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);

    @Query("""
                SELECT r
                FROM Room r
                WHERE r.branch.id = :branchId
                AND r.status = 'ACTIVE'
                AND r.id NOT IN (
                    SELECT res.room.id
                    FROM Reservation res
                    WHERE res.status <> 'CANCELLED'
                    AND (
                        :checkIn < res.checkOutTime
                        AND :checkOut > res.checkInTime
                    )
                )
            """)
    List<Room> findAvailableRooms(
            @Param("branchId") Long branchId,
            @Param("checkIn") LocalDateTime checkIn,
            @Param("checkOut") LocalDateTime checkOut);

    @Query("""
                SELECT r FROM Reservation r
                LEFT JOIN FETCH r.user
                LEFT JOIN FETCH r.branch
                LEFT JOIN FETCH r.table
                LEFT JOIN FETCH r.room
                WHERE r.id = :id
            """)
    Optional<Reservation> findByIdWithDetails(@Param("id") Long id);

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE r.status = 'CONFIRMED'
            AND r.checkInTime BETWEEN :from AND :to
            """)
    List<Reservation> findUpcomingReservations(
            LocalDateTime from,
            LocalDateTime to);

    @Query("SELECT r FROM Reservation r WHERE r.status = 'CONFIRMED' AND r.checkOutTime > :now")
    List<Reservation> findConfirmedActiveReservations(@Param("now") LocalDateTime now);
}
