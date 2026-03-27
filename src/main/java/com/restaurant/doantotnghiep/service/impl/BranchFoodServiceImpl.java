package com.restaurant.doantotnghiep.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.restaurant.doantotnghiep.dto.FoodWithPromotionDTO;
import com.restaurant.doantotnghiep.entity.BranchFood;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.Promotion;
import com.restaurant.doantotnghiep.repository.BranchFoodRepository;
import com.restaurant.doantotnghiep.repository.FoodRepository;
import com.restaurant.doantotnghiep.repository.PromotionRepository;
import com.restaurant.doantotnghiep.service.BranchFoodService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchFoodServiceImpl implements BranchFoodService {

    private final BranchFoodRepository repository;
    private final PromotionRepository promotionRepository;
    private final FoodRepository foodRepository;

    @Override
    public BranchFood save(BranchFood bf) {
        return repository.save(bf);
    }

    @Override
    public List<BranchFood> getByBranch(Long branchId) {
        return repository.findByBranchId(branchId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodWithPromotionDTO> getFoodsWithPromotionByBranch(Long branchId) {
        List<Food> allFoods = foodRepository.findAll();
        System.out.println("📦 Tổng foods: " + allFoods.size());

        if (allFoods.isEmpty()) {
            System.out.println("KHÔNG CÓ FOODS!");
            return List.of();
        }

        // Lấy branch_foods đã phân phối
        List<BranchFood> branchFoods = repository.findByBranchIdWithFood(branchId);
        System.out.println("🏢 Đã phân phối: " + branchFoods.size());

        // Map để tra cứu
        var branchFoodMap = branchFoods.stream()
                .collect(Collectors.toMap(
                        bf -> bf.getFood().getId(),
                        bf -> bf,
                        (existing, replacement) -> existing));

        LocalDate today = LocalDate.now();

        // Build DTO
        return allFoods.stream()
                .map(food -> {
                    BranchFood bf = branchFoodMap.get(food.getId());

                    BigDecimal branchPrice;
                    Boolean isActive;
                    Integer stockQuantity;
                    Long branchFoodId = null;

                    if (bf != null) {
                        // Đã phân phối
                        branchPrice = (bf.getCustomPrice() != null && bf.getCustomPrice() > 0)
                                ? BigDecimal.valueOf(bf.getCustomPrice())
                                : food.getPrice();
                        isActive = bf.getIsActive();
                        stockQuantity = bf.getStockQuantity();
                        branchFoodId = bf.getId();
                    } else {
                        // Chưa phân phối
                        branchPrice = food.getPrice();
                        isActive = false;
                        stockQuantity = 0;
                    }

                    FoodWithPromotionDTO dto = FoodWithPromotionDTO.builder()
                            .id(food.getId())
                            .name(food.getName())
                            .description(food.getDescription())
                            .originalPrice(food.getPrice())
                            .branchPrice(branchPrice)
                            .finalPrice(branchPrice)
                            .imageUrl(food.getImageUrl())
                            .stockQuantity(stockQuantity)
                            .isActive(isActive)
                            .hasPromotion(false)
                            .branchFoodId(branchFoodId)
                            .branchId(branchId)
                            .build();

                    // Khuyến mãi (chỉ cho sản phẩm đã phân phối)
                    if (bf != null) {
                        List<Promotion> activePromotions = promotionRepository.findAll().stream()
                                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                                .filter(p -> p.getStartDate() == null || !p.getStartDate().isAfter(today))
                                .filter(p -> p.getEndDate() == null || !p.getEndDate().isBefore(today))
                                .filter(p -> p.getFoods() != null &&
                                        p.getFoods().stream()
                                                .anyMatch(f -> f.getId().equals(food.getId())))
                                .filter(p -> p.getBranches() != null &&
                                        p.getBranches().stream()
                                                .anyMatch(b -> b.getId().equals(branchId)))
                                .toList();

                        if (!activePromotions.isEmpty()) {
                            Promotion bestPromotion = findBestPromotion(activePromotions, branchPrice);

                            dto.setHasPromotion(true);
                            dto.setPromotionId(bestPromotion.getId());
                            dto.setPromotionName(bestPromotion.getName());
                            dto.setDiscountPercentage(bestPromotion.getDiscountPercentage());
                            dto.setDiscountAmount(bestPromotion.getDiscountAmount());
                            dto.setFinalPrice(calculateFinalPrice(branchPrice, bestPromotion));
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Promotion findBestPromotion(List<Promotion> promotions, BigDecimal basePrice) {
        return promotions.stream()
                .max((p1, p2) -> {
                    BigDecimal discount1 = calculateDiscount(basePrice, p1);
                    BigDecimal discount2 = calculateDiscount(basePrice, p2);
                    return discount1.compareTo(discount2);
                })
                .orElse(promotions.get(0));
    }

    private BigDecimal calculateDiscount(BigDecimal basePrice, Promotion promotion) {
        if (promotion.getDiscountPercentage() != null) {
            return basePrice
                    .multiply(promotion.getDiscountPercentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (promotion.getDiscountAmount() != null) {
            return promotion.getDiscountAmount();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateFinalPrice(BigDecimal basePrice, Promotion promotion) {
        BigDecimal discount = calculateDiscount(basePrice, promotion);
        BigDecimal finalPrice = basePrice.subtract(discount);

        // Đảm bảo giá không âm
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        return finalPrice.setScale(0, RoundingMode.HALF_UP);
    }

    @Override
    public Optional<BranchFood> findById(Long id) {
        return repository.findById(id);
    }
}
