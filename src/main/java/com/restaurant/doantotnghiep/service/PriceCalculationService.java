package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.entity.BranchFood;
import com.restaurant.doantotnghiep.entity.Promotion;
import com.restaurant.doantotnghiep.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceCalculationService {

    private final PromotionRepository promotionRepository;

    public BigDecimal calculateFinalPrice(BranchFood branchFood) {
        BigDecimal basePrice = resolveBasePrice(branchFood);

        List<Promotion> activePromos = promotionRepository
                .findActivePromotionsForBranchFood(
                        branchFood.getFood().getId(),
                        branchFood.getBranch().getId(),
                        LocalDate.now()
                );

        if (activePromos.isEmpty()) {
            return basePrice;
        }

        // Lấy promotion giảm nhiều nhất
        BigDecimal maxDiscount = BigDecimal.ZERO;
        for (Promotion promo : activePromos) {
            BigDecimal discount = calcDiscount(basePrice, promo);
            if (discount.compareTo(maxDiscount) > 0) {
                maxDiscount = discount;
            }
        }

        BigDecimal finalPrice = basePrice.subtract(maxDiscount);
        return finalPrice.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO
                : finalPrice.setScale(0, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveBasePrice(BranchFood branchFood) {
        if (branchFood.getCustomPrice() != null && branchFood.getCustomPrice() > 0) {
            return BigDecimal.valueOf(branchFood.getCustomPrice());
        }
        return branchFood.getFood().getPrice();
    }

    private BigDecimal calcDiscount(BigDecimal basePrice, Promotion promo) {
        if (promo.getDiscountPercentage() != null) {
            return basePrice
                    .multiply(promo.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        if (promo.getDiscountAmount() != null) {
            return promo.getDiscountAmount();
        }
        return BigDecimal.ZERO;
    }
}