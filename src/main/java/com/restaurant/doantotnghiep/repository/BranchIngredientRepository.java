package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.BranchIngredient;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BranchIngredientRepository extends JpaRepository<BranchIngredient, Long> {

    List<BranchIngredient> findByBranchId(Long branchId);

    Optional<BranchIngredient> findByBranchIdAndIngredientId(Long branchId, Long ingredientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
                SELECT bi FROM BranchIngredient bi
                WHERE bi.branch.id = :branchId
                AND bi.ingredient.id = :ingredientId
            """)
    BranchIngredient findByBranchIdAndIngredientIdForUpdate(
            Long branchId,
            Long ingredientId);
}
