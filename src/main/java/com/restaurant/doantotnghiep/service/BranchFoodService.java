package com.restaurant.doantotnghiep.service;

import java.util.List;
import java.util.Optional;

import com.restaurant.doantotnghiep.dto.FoodWithPromotionDTO;
import com.restaurant.doantotnghiep.entity.BranchFood;

public interface BranchFoodService {
    BranchFood save(BranchFood bp);

    List<BranchFood> getByBranch(Long branchId);

    List<FoodWithPromotionDTO> getFoodsWithPromotionByBranch(Long branchId);

    Optional<BranchFood> findById(Long id);
}
