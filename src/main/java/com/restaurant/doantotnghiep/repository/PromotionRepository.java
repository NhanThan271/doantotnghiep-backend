package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    @Query("""
                SELECT DISTINCT p FROM Promotion p
                LEFT JOIN FETCH p.foods
                LEFT JOIN FETCH p.branches
            """)
    List<Promotion> findAllWithFoods();

    @Query("""
                SELECT p FROM Promotion p
                LEFT JOIN FETCH p.foods
                LEFT JOIN FETCH p.branches
                WHERE p.id = :id
            """)
    Optional<Promotion> findByIdWithFoods(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Promotion p " +
            "LEFT JOIN FETCH p.foods " +
            "LEFT JOIN FETCH p.branches")
    List<Promotion> findAllWithRelations();

    @Query("""
                SELECT DISTINCT p FROM Promotion p
                JOIN p.foods f
                JOIN p.branches b
                WHERE f.id = :foodId
                  AND b.id = :branchId
                  AND p.isActive = true
                  AND (p.startDate IS NULL OR p.startDate <= :today)
                  AND (p.endDate IS NULL OR p.endDate >= :today)
            """)
    List<Promotion> findActivePromotionsForBranchFood(
            @Param("foodId") Long foodId,
            @Param("branchId") Long branchId,
            @Param("today") LocalDate today);

}
