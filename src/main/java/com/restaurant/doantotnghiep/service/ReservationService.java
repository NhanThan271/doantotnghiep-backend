package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.ReservationResponse;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;

import java.util.List;
import java.util.Map;

public interface ReservationService {

    Reservation createFullReservation(Map<String, Object> request);

    List<ReservationResponse> getPendingReservations();

    Reservation updateStatus(Long reservationId, ReservationStatus status);
}
