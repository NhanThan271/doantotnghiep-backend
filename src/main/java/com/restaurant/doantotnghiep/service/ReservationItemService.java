package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.ReservationItem;

import java.util.List;

public interface ReservationItemService {

    ReservationItem create(Long reservationId, Long branchFoodId, Integer quantity);

    ReservationItem update(Long id, Integer quantity);

    void delete(Long id);

    List<ReservationItem> getByReservation(Long reservationId);

    ReservationItem getById(Long id);
}