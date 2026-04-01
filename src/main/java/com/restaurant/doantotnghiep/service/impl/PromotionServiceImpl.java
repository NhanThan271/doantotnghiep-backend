package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.FoodDTO;
import com.restaurant.doantotnghiep.dto.PromotionDTO;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.entity.Food;
import com.restaurant.doantotnghiep.entity.Promotion;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.repository.FoodRepository;
import com.restaurant.doantotnghiep.repository.PromotionRepository;
import com.restaurant.doantotnghiep.service.PromotionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final FoodRepository foodRepository;
    private final BranchRepository branchRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository, FoodRepository foodRepository,
            BranchRepository branchRepository) {
        this.promotionRepository = promotionRepository;
        this.foodRepository = foodRepository;
        this.branchRepository = branchRepository;
    }

    // ===================== GET ALL =====================
    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAllWithFoods()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ===================== GET BY ID =====================
    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionDTO> getPromotionById(Long id) {
        return promotionRepository.findByIdWithFoods(id)
                .map(this::mapToDTO);
    }

    // ===================== CREATE =====================
    @Override
    @Transactional
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = new Promotion();
        promotion.setName(promotionDTO.getName());
        promotion.setDescription(promotionDTO.getDescription());
        promotion.setDiscountPercentage(promotionDTO.getDiscountPercentage());
        promotion.setDiscountAmount(promotionDTO.getDiscountAmount());
        promotion.setStartDate(promotionDTO.getStartDate());
        promotion.setEndDate(promotionDTO.getEndDate());
        promotion.setIsActive(promotionDTO.getIsActive());
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        // Lấy foods từ foodIds
        if (promotionDTO.getFoodIds() != null && !promotionDTO.getFoodIds().isEmpty()) {
            Set<Food> foods = new HashSet<>(
                    foodRepository.findAllById(promotionDTO.getFoodIds()));
            promotion.setFoods(foods);
        }

        // Lấy branches từ branchIds
        if (promotionDTO.getBranchIds() != null && !promotionDTO.getBranchIds().isEmpty()) {
            Set<Branch> branches = new HashSet<>(
                    branchRepository.findAllById(promotionDTO.getBranchIds()));

            System.out.println("Branches found: " + branches.size()); // Debug log

            promotion.setBranches(branches);
        } else {
            promotion.setBranches(new HashSet<>());
        }

        // Save và flush để đảm bảo cascade
        Promotion saved = promotionRepository.saveAndFlush(promotion);

        // Fetch lại để có đầy đủ data
        Promotion fetched = promotionRepository.findByIdWithFoods(saved.getId())
                .orElseThrow(() -> new RuntimeException("Failed to fetch saved promotion"));

        return mapToDTO(fetched);
    }

    // ===================== UPDATE =====================
    @Override
    @Transactional
    public PromotionDTO updatePromotion(Long id, PromotionDTO promotionDTO) {
        return promotionRepository.findByIdWithFoods(id)
                .map(existing -> {
                    existing.setName(promotionDTO.getName());
                    existing.setDescription(promotionDTO.getDescription());
                    existing.setDiscountPercentage(promotionDTO.getDiscountPercentage());
                    existing.setDiscountAmount(promotionDTO.getDiscountAmount());
                    existing.setStartDate(promotionDTO.getStartDate());
                    existing.setEndDate(promotionDTO.getEndDate());
                    existing.setIsActive(promotionDTO.getIsActive());
                    existing.setUpdatedAt(LocalDateTime.now());

                    // Cập nhật foods từ foodIds
                    if (promotionDTO.getFoodIds() != null) {
                        Set<Food> foods = new HashSet<>(
                                foodRepository.findAllById(promotionDTO.getFoodIds()));
                        existing.setFoods(foods);
                    }

                    if (promotionDTO.getBranchIds() != null && !promotionDTO.getBranchIds().isEmpty()) {
                        Set<Branch> branches = new HashSet<>(
                                branchRepository.findAllById(promotionDTO.getBranchIds()));
                        existing.setBranches(branches);
                    } else {
                        existing.setBranches(new HashSet<>());
                    }

                    Promotion updated = promotionRepository.save(existing);
                    return mapToDTO(updated);
                })
                .orElseThrow(() -> new RuntimeException("Promotion not found with id " + id));
    }

    // ===================== DELETE =====================
    @Override
    @Transactional
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    // ===================== MAPPING =====================
    private PromotionDTO mapToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setDescription(promotion.getDescription());
        dto.setDiscountPercentage(promotion.getDiscountPercentage());
        dto.setDiscountAmount(promotion.getDiscountAmount());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setIsActive(promotion.getIsActive());
        dto.setCreatedAt(promotion.getCreatedAt());
        dto.setUpdatedAt(promotion.getUpdatedAt());

        // Map foodIds
        if (promotion.getFoods() != null && !promotion.getFoods().isEmpty()) {
            dto.setFoodIds(
                    promotion.getFoods().stream()
                            .map(Food::getId)
                            .collect(Collectors.toList()));
            dto.setFoods(
                    promotion.getFoods().stream()
                            .map(this::mapFoodToDTO)
                            .toList());
        } else {
            dto.setFoodIds(new ArrayList<>());
            dto.setFoods(new ArrayList<>());
        }
        // Map foods
        if (promotion.getFoods() != null && !promotion.getFoods().isEmpty()) {
            dto.setFoods(
                    promotion.getFoods().stream()
                            .map(this::mapFoodToDTO) // ← Phải có method này
                            .toList());
        } else {
            dto.setFoods(new ArrayList<>());
        }
        // Map branchIds
        if (promotion.getBranches() != null && !promotion.getBranches().isEmpty()) {
            try {
                dto.setBranchIds(
                        promotion.getBranches().stream()
                                .map(Branch::getId)
                                .collect(Collectors.toList()));
            } catch (Exception e) {
                System.err.println("Error mapping branches: " + e.getMessage());
                dto.setBranchIds(new ArrayList<>());
            }
        } else {
            dto.setBranchIds(new ArrayList<>());
        }

        return dto;
    }

    private FoodDTO mapFoodToDTO(Food food) {
        FoodDTO dto = new FoodDTO();
        dto.setId(food.getId());
        dto.setName(food.getName());
        dto.setDescription(food.getDescription());
        dto.setPrice(food.getPrice());
        dto.setImageUrl(food.getImageUrl());
        dto.setIsActive(food.getIsActive());
        return dto;
    }
}
