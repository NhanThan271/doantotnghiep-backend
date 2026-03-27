package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByNameContainingIgnoreCase(String name);

    List<Shift> findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalTime start, LocalTime end);
}