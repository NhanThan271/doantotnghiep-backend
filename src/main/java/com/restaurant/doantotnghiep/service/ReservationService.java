package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;

import java.util.List;
import java.util.Map;

public interface ReservationService {

    Reservation createFullReservation(Map<String, Object> request);

    List<Reservation> getPendingReservations();

    Reservation updateStatus(Long reservationId, ReservationStatus status);
}
