package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.ReservationResponse;
import com.restaurant.doantotnghiep.dto.SeatMapResponse;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;

import java.util.List;
import java.util.Map;

public interface ReservationService {

    Reservation createFullReservation(Map<String, Object> request);

    List<ReservationResponse> getReservationsByStatus(ReservationStatus status);

    Reservation updateStatus(Long reservationId, ReservationStatus status);

    List<SeatMapResponse> getTableMap();

    List<SeatMapResponse> getRoomMap();
}
