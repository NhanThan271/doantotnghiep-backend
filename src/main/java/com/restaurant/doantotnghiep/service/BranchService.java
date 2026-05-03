package com.restaurant.doantotnghiep.service;

import com.restaurant.doantotnghiep.dto.BranchNearestResponse;
import com.restaurant.doantotnghiep.entity.Branch;

import java.util.List;

public interface BranchService {
    Branch create(Branch branch);

    List<Branch> getAll();

    Branch getById(Long id);

    Branch update(Long id, Branch branch);

    void delete(Long id);

    BranchNearestResponse findNearest(double userLat, double userLng);
}
