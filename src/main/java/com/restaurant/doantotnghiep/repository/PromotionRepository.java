package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
