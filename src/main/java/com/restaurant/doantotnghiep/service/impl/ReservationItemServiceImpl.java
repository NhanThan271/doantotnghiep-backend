package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.entity.*;
import com.restaurant.doantotnghiep.repository.*;
import com.restaurant.doantotnghiep.service.ReservationItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationItemServiceImpl implements ReservationItemService {

    private final ReservationItemRepository repository;
    private final ReservationRepository reservationRepository;
    private final BranchFoodRepository branchFoodRepository;

    @Override
    public ReservationItem create(Long reservationId, Long branchFoodId, Integer quantity) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        BranchFood branchFood = branchFoodRepository.findById(branchFoodId)
                .orElseThrow(() -> new RuntimeException("BranchFood not found"));

        if (branchFood.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        Double price = branchFood.getCustomPrice() != null
                ? branchFood.getCustomPrice()
                : branchFood.getFood().getPrice().doubleValue();

        ReservationItem item = ReservationItem.builder()
                .reservation(reservation)
                .branchFood(branchFood)
                .quantity(quantity)
                .price(BigDecimal.valueOf(price))
                .build();

        return repository.save(item);
    }

    @Override
    public ReservationItem update(Long id, Integer quantity) {
        ReservationItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ReservationItem not found"));

        item.setQuantity(quantity);

        return repository.save(item);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("ReservationItem not found");
        }
        repository.deleteById(id);
    }

    @Override
    public List<ReservationItem> getByReservation(Long reservationId) {
        return repository.findByReservationId(reservationId);
    }

    @Override
    public ReservationItem getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ReservationItem not found"));
    }
}