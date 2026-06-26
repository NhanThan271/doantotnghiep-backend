package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.ReservationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {

    List<ReservationItem> findByReservationId(Long reservationId);

    @Query("""
                SELECT ri FROM ReservationItem ri
                JOIN FETCH ri.branchFood bf
                JOIN FETCH bf.food f
                JOIN FETCH ri.reservation r
                WHERE r.branch.id = :branchId
                  AND r.checkInTime >= :from
                  AND r.checkInTime <= :to
                  AND r.status IN ('CONFIRMED', 'CHECKED_IN')
            """)
    List<ReservationItem> findItemsForPreparation(
            @Param("branchId") Long branchId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}