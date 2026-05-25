package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.KitchenOrder;
import com.restaurant.doantotnghiep.entity.enums.KitchenOrderStatus;
import com.restaurant.doantotnghiep.entity.Reservation;
import com.restaurant.doantotnghiep.entity.ReservationItem;

import java.util.List;

public interface KitchenOrderService {

    KitchenOrder create(Long orderId);

    KitchenOrder updateStatus(Long id, KitchenOrderStatus status);

    List<KitchenOrder> getByOrder(Long orderId);

    List<KitchenOrder> getByStatus(KitchenOrderStatus status);

    KitchenOrder createFromReservation(Reservation reservation, List<ReservationItem> items);

    List<KitchenOrder> getActiveKitchenOrders();

    KitchenOrder getById(Long id);
}