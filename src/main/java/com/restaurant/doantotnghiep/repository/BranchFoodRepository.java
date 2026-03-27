package com.restaurant.doantotnghiep.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.restaurant.doantotnghiep.entity.BranchFood;

public interface BranchFoodRepository extends JpaRepository<BranchFood, Long> {

    List<BranchFood> findByBranchId(Long branchId);

    @Query("""
                SELECT bp FROM BranchFood bp
                JOIN FETCH bp.food p
                JOIN FETCH bp.branch b
                WHERE bp.branch.id = :branchId
                AND bp.isActive = true
                AND p.isActive = true
            """)
    List<BranchFood> findByBranchIdWithFood(@Param("branchId") Long branchId);

    @Query("""
                SELECT bp FROM BranchFood bp
                JOIN FETCH bp.food p
                WHERE bp.branch.id = :branchId
                AND bp.food.id = :foodId
                AND bp.isActive = true
            """)
    Optional<BranchFood> findByBranchIdAndFoodId(
            @Param("branchId") Long branchId,
            @Param("foodId") Long foodId);

    @Query("SELECT bp FROM BranchFood bp WHERE bp.branch.id = :branchId AND bp.isActive = true")
    List<BranchFood> findAllByBranchId(@Param("branchId") Long branchId);

}
