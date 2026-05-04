package com.restaurant.doantotnghiep.service.impl;

import com.restaurant.doantotnghiep.dto.BranchNearestResponse;
import com.restaurant.doantotnghiep.entity.Branch;
import com.restaurant.doantotnghiep.repository.BranchRepository;
import com.restaurant.doantotnghiep.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public Branch create(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public List<Branch> getAll() {
        return branchRepository.findAll();
    }

    @Override
    public Branch getById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
    }

    @Override
    public Branch update(Long id, Branch branch) {
        Branch existing = getById(id);
        existing.setName(branch.getName());
        existing.setAddress(branch.getAddress());
        existing.setPhone(branch.getPhone());
        existing.setLatitude(branch.getLatitude());
        existing.setLongitude(branch.getLongitude());
        existing.setIsActive(branch.getIsActive());
        return branchRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Branch branch = getById(id);
        branch.setIsActive(false);
        branchRepository.save(branch);
    }

    @Override
    public BranchNearestResponse findNearest(double userLat, double userLng) {

        List<Branch> branches = branchRepository.findByIsActiveTrue();

        if (branches.isEmpty()) {
            throw new RuntimeException("Không có chi nhánh nào");
        }

        Branch nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Branch b : branches) {

            if (b.getLatitude() == null || b.getLongitude() == null) {
                continue;
            }

            double distance = calculateDistance(
                    userLat, userLng,
                    b.getLatitude(), b.getLongitude());

            if (distance < minDistance) {
                minDistance = distance;
                nearest = b;
            }
        }

        if (nearest == null) {
            throw new RuntimeException("Không có chi nhánh hợp lệ");
        }

        double roundedDistance = Math.round(minDistance * 100.0) / 100.0;
        String text = roundedDistance + " km";

        return new BranchNearestResponse(nearest, roundedDistance, text);
    }

    // Công thức tính khoản cách giữa 2 điểm trên bản đồ
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

}
