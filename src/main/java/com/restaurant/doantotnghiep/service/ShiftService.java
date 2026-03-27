package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Shift;

import java.time.LocalTime;
import java.util.List;

public interface ShiftService {

    Shift create(Shift shift);

    Shift update(Long id, Shift shift);

    void delete(Long id);

    Shift getById(Long id);

    List<Shift> getAll();

    List<Shift> searchByName(String name);

    List<Shift> getByTimeRange(LocalTime start, LocalTime end);
}