package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.PromotionDTO;

import java.util.List;
import java.util.Optional;

public interface PromotionService {

    List<PromotionDTO> getAllPromotions();

    Optional<PromotionDTO> getPromotionById(Long id);

    PromotionDTO createPromotion(PromotionDTO promotionDTO);

    PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO);

    void deletePromotion(Long id);
}
