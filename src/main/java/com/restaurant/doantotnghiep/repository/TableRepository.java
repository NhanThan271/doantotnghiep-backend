package com.restaurant.doantotnghiep.repository;

import com.restaurant.doantotnghiep.entity.TableEntity;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
    // tìm tất cả bàn theo chi nhánh
    List<TableEntity> findByBranchId(Long branchId);

    // kiểm tra số bàn đã tồn tại trong chi nhánh chưa
    boolean existsByBranchIdAndNumberAndArea(Long branchId, Integer number, String area);

    // Tìm bàn theo chi nhánh và số bàn
    Optional<TableEntity> findByBranchIdAndNumberAndArea(Long branchId, Integer number, String area);

    // Kiểm tra trùng khi update (loại trừ bàn hiện tại)
    boolean existsByBranchIdAndNumberAndAreaAndIdNot(
            Long branchId,
            Integer number,
            String area,
            Long id);

    // Tìm tất cả bàn theo branch và area
    List<TableEntity> findByBranchIdAndArea(Long branchId, String area);

    // Lấy danh sách area DISTINCT theo branch
    @Query("SELECT DISTINCT t.area FROM TableEntity t WHERE t.branch.id = :branchId ORDER BY t.area")
    List<String> findDistinctAreasByBranchId(@Param("branchId") Long branchId);
}
