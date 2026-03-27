package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.enums.ReservationStatus;

import java.util.List;

public interface ReservationService {

    Reservation createReservation(Reservation reservation);

    List<Reservation> getPendingReservations();

    Reservation updateStatus(Long reservationId, ReservationStatus status);
}
