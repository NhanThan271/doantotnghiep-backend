package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByNameContainingIgnoreCase(String name);

    List<Food> findByCategoryId(Long categoryId);

    List<Food> findByIsActiveTrue();
}
